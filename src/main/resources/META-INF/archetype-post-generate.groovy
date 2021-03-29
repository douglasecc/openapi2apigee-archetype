import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.type.TypeReference
@Grapes([
        @Grab(group='org.openapitools', module='openapi-generator', version='4.2.1'),
        @Grab(group='org.apache.velocity', module='velocity', version='1.7'),
        @Grab(group='org.apache.commons', module='commons-lang3', version='3.4'),
        @Grab(group='com.google.code.gson', module='gson', version='2.8.6'),
        @Grab(group='org.yaml', module='snakeyaml', version='1.25')
])

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.google.gson.Gson
import org.yaml.snakeyaml.Yaml
import com.sun.org.apache.xml.internal.serialize.OutputFormat
import com.sun.org.apache.xml.internal.serialize.XMLSerializer
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.PathItem
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.parser.ObjectMapperFactory
import io.swagger.v3.parser.OpenAPIV3Parser
import io.swagger.v3.parser.core.models.AuthorizationValue
import io.swagger.v3.parser.core.models.SwaggerParseResult
import io.swagger.v3.parser.util.ClasspathHelper
import io.swagger.v3.parser.util.RemoteUrl
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.apache.commons.lang3.StringUtils
import org.apache.velocity.Template
import org.apache.velocity.VelocityContext
import org.apache.velocity.app.VelocityEngine
import org.openapitools.codegen.ClientOptInput
import org.openapitools.codegen.CodegenConstants
import org.openapitools.codegen.DefaultGenerator
import org.openapitools.codegen.config.CodegenConfigurator
import org.openapitools.codegen.config.GlobalSettings
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.w3c.dom.Document

import javax.net.ssl.SSLHandshakeException
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import java.nio.file.Files
import java.nio.file.LinkOption
import java.nio.file.Path
import java.nio.file.Paths

import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonParser

import org.apache.maven.archetype.ArchetypeGenerationRequest

class OpenAPIV3ParserData extends OpenAPIV3Parser {

    private static final Logger LOGGER = LoggerFactory.getLogger(OpenAPIV3Parser.class)
    private static ObjectMapper JSON_MAPPER = ObjectMapperFactory.createJson()
    private static ObjectMapper YAML_MAPPER = ObjectMapperFactory.createYaml()
    private static String encoding = "UTF-8"
    private String data

    private ObjectMapper getRightMapper(String data) {
        ObjectMapper mapper
        if (data.trim().startsWith("{")) {
            mapper = JSON_MAPPER
        } else {
            mapper = YAML_MAPPER
        }
        return mapper
    }

    SwaggerParseResult readWithInfo(String location, List<AuthorizationValue> auths) {
        SwaggerParseResult output
        try {
            location = location.replaceAll("\\\\", "/")
            if (location.toLowerCase().startsWith("http")) {
                data = RemoteUrl.urlToString(location, auths)

            } else {
                String fileScheme = "file:"
                Path path
                if (location.toLowerCase().startsWith("file:")) {
                    path = Paths.get(URI.create(location))
                } else {
                    path = Paths.get(location)
                }

                if (Files.exists(path, new LinkOption[0])) {
                    data = FileUtils.readFileToString(path.toFile(), encoding)
                } else {
                    data = ClasspathHelper.loadFileFromClasspath(location)
                }
            }
            LOGGER.debug("Loaded raw data: {}", data)
            ObjectMapper mapper = this.getRightMapper(data)
            JsonNode rootNode = mapper.readTree(data)
            LOGGER.debug("Parsed rootNode: {}", rootNode)
            return this.readWithInfo(location, rootNode)

        } catch (SSLHandshakeException var6) {
            output = new SwaggerParseResult()
            output.setMessages(Arrays.asList("unable to read location `" + location + "` due to a SSL configuration error.  It is possible that the server SSL certificate is invalid, self-signed, or has an untrusted Certificate Authority."))
            return output

        } catch (Exception var7) {
            LOGGER.warn("Exception while reading:", var7)
            output = new SwaggerParseResult()
            output.setMessages(Arrays.asList("unable to read location `" + location + "`"))
            return output
        }
    }

    String getSpecData() {
        return data
    }
}

class TemplateReplacer {

    private final VelocityEngine velocityEngine

    private final VelocityContext context

    private final String basepath

    TemplateReplacer(File rootTemplateFolder, Properties templateVariables) {
        velocityEngine = new VelocityEngine()
        context = new VelocityContext()
        basepath = rootTemplateFolder.path
        context.put("file.resource.loader.path", basepath)
        context.put("file.resource.loader.cache", false)
        context.put("velocimacro.library.autoreload", true)
        for (String key: templateVariables.keySet()) {
            context.put(key, templateVariables.get(key))
        }
    }

    void merge(File file) {
        String resource = file.path.substring(basepath.length() + 1)
        Template template = velocityEngine.getTemplate(resource)
        FileWriter fileWriter = new FileWriter(file)
        template.merge(context, fileWriter)
        fileWriter.close()
    }
}

class APIProxyFlow {

    String name
    String desc
    String path
    String verb

    @Override
    public String toString() {
        return "APIProxyFlow{" +
                "name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                ", path='" + path + '\'' +
                ", verb='" + verb + '\'' +
                '}';
    }
}

interface FileExecutor {
    void execute(File file)
}

class FileWalker {

    private final FileExecutor executor

    FileWalker(FileExecutor executor) {
        this.executor = executor
    }

    void walk(File folder) {
        for (File file: folder.listFiles()) {
            if (file.isDirectory()) {
                walk(file)
            } else {
                executor.execute(file)
            }
        }
    }
}

class TemplateExecutor implements FileExecutor {

    private final TemplateReplacer templateRunner

    TemplateExecutor(File rootTemplateFolder, Properties templateVariables) {
        templateRunner = new TemplateReplacer(rootTemplateFolder, templateVariables)
    }

    @Override
    void execute(File file) {
        if (file.name.endsWith(".vm") || "pom.xml".equals(file.name)) {
            templateRunner.merge(file)
        }
    }
}

class FileRenamer implements FileExecutor {
    @Override
    void execute(File file) {
        if (file.name.endsWith(".vm")) {
            file.renameTo(new File(file.path.substring(0,file.path.length()-3)))
        }
    }
}

class RootFilesMover implements FileExecutor {
    @Override
    void execute(File file) {
        FileUtils.moveFile(file, new File(file.parentFile.parentFile.path + "/" + file.name))
    }
}

class XmlFormater implements FileExecutor {

    final DocumentBuilderFactory dbFactory
    final DocumentBuilder dBuilder

    XmlFormater() {
        dbFactory = DocumentBuilderFactory.newInstance()
        dBuilder = dbFactory.newDocumentBuilder()
    }

    @Override
    void execute(File file) {
        if (file.name.endsWith(".xml")) {
            Document doc = dBuilder.parse(file)
            OutputFormat format = new OutputFormat(doc)
            format.setIndenting(true)
            format.setLineWidth(255)
            format.setIndent(4)
            Writer out = new StringWriter()
            XMLSerializer serializer = new XMLSerializer(out, format)
            serializer.serialize(doc)
            FileWriter fileWriter = new FileWriter(file)
            fileWriter.write(out.toString().replaceAll("&quot;", "\""))
            fileWriter.close()
        }
    }
}

class ProfileSetup {

    private final Properties props

    ProfileSetup(Properties props) {
        this.props = props
    }

    void dumpMap(Map map, int level) {
        String padding = ""
        for (int i = 0; i < (level * 2); i++) {
            padding += " "
        }
        for (Object key: map.keySet()) {
            Object value = map.get(key)
            if (value instanceof Map) {
                System.out.println(padding + key + ":")
                dumpMap((Map) value, level + 1)
            } else {
                System.out.println(padding + key + ": " + value.toString())
            }
        }
    }

    private Map<String, Map<String, String>> getFromParameters() {
        Map<String, Map<String, String>> setupMap = new HashMap<>()
        for (String env : props.get("envs").split(",")) {
            Map<String, String> envMap = new HashMap<>()
            envMap.put("org", props.getProperty("org"))
            envMap.put("env", env)
            envMap.put("envs", props.get("envs").split(","))
            envMap.put("profile", env)
            envMap.put("emailDev", props.getProperty("emailDev"))
            envMap.put("emailPO", props.getProperty("emailPO"))
            envMap.put("loginPO", props.getProperty("loginPO"))
            envMap.put("emailTest", props.getProperty("emailTest"))
            envMap.put("loginTest", props.getProperty("loginTest"))
            envMap.put("authtype", props.getProperty("authtype"))
            envMap.put("tsAuthType", props.getProperty("tsAuthType"))
            envMap.put("targetServer", props.getProperty("targetServers"))
            envMap.put("virtualhost", props.getProperty("virtualhost"))
            envMap.put("targeturl", props.getProperty("targeturl"))
            envMap.put("options", props.getProperty("options"))
            envMap.put("delay", props.getProperty("delay"))
            envMap.put("tokenurl", props.getProperty("tokenurl"))
            envMap.put("clientid", props.getProperty("clientid"))
            envMap.put("clientsecret", props.getProperty("clientsecret"))
            envMap.put("proxydomain", props.getProperty("proxydomain"))
            envMap.put("proxyscheme", props.getProperty("proxyscheme"))
            envMap.put("proxyport", props.getProperty("proxyport"))
            envMap.put("hosturl", props.getProperty("hosturl"))
            envMap.put("apiversion", props.getProperty("apiversion"))
            envMap.put("config-options", props.getProperty("config-options"))
            envMap.put("config-exportdir", props.getProperty("config-exportdir"))
            envMap.put("config-dir", props.getProperty("config-dir"))
            envMap.put("config-dir", props.getProperty("config-dir"))
            envMap.put("mockserver", props.getProperty("mockserver"))

            if ("true".equals(props.get("mockserver"))) {
                String targetUrl = props.getProperty("proxyscheme") + "://" + props.getProperty("proxydomain") + ":" +  props.getProperty("proxyport") + '/mock' +  props.getProperty("basepath")
                envMap.put("targeturl", targetUrl)
            } else {
                envMap.put("targeturl", props.getProperty("targeturl"))
            }

            setupMap.put(env, envMap)
        }
        return setupMap
    }

    private Map<String, String> getDefaultValues() {
        Map<String, String> setup = new HashMap<>()
        setup.put("hosturl", "https://api.enterprise.apigee.com")
        setup.put("apiversion", "v1")
        setup.put("options", "update")
        setup.put("delay", "1000")
        setup.put("tokenurl", "https://login.apigee.com/oauth/token")
        setup.put("authtype", "basic")
        setup.put("clientid", "edgecli")
        setup.put("clientsecret", "edgeclisecret")
        setup.put("proxyscheme", "https")
        setup.put("proxyport", "443")
        setup.put("config-options", "update")
        setup.put("config-exportdir", "target")
        setup.put("config-dir", "target/edge")
        setup.put("enable-cors", "false")
        setup.put("cors-policy-name", "AM-AddCORS")
        return setup
    }

    private Map<String, Map<String, String>> getFromSetupFile() {
        final String path = props.getProperty("edgeSetupFile")
        File setupFile = new File(path)
        String content = FileUtils.readFileToString(setupFile)
        final ObjectMapper mapper;
        if (content.trim().startsWith("{")) {
            mapper = new ObjectMapper()
        } else {
            mapper = new ObjectMapper(new YAMLFactory())
        }
        Map<String, Object> settings = mapper.readValue(content, new TypeReference<Map<String, Object>>() {})
        Map<String, Map<String, Object>> profiles = new HashMap<>()
        for (String org: settings.keySet()) {
            Map<String, Object> baseProfile = new HashMap<>()
            for (Object setting: ((Map) settings.get(org)).keySet()) {
                if (!"profiles".equals(setting)) {
                    baseProfile.put(setting, ((Map) settings.get(org)).get(setting))
                }
            }
            if (!"additional-settings".equals(org)) {
                for (Object profile : ((Map) ((Map) settings.get(org)).get("profiles")).keySet()) {
                    Map<String, String> profileMap = new HashMap<>()
                    profileMap.putAll(getDefaultValues())
                    profileMap.putAll(baseProfile)
                    profileMap.putAll((Map) ((Map) ((Map) settings.get(org)).get("profiles")).get(profile))
                    profileMap.put('org', org)
                    if (!profileMap.containsKey("env")) {
                        profileMap.put("env", ((String) profile))
                    }
                    if (!profileMap.containsKey("profile")) {
                        profileMap.put('profile', org + "-" + ((String) profile))
                        profiles.put(org + "-" + ((String) profile), profileMap)
                    } else {
                        profiles.put((String) profileMap.get("profile"), profileMap)
                    }

                    if (profileMap.containsKey("mockserver") && profileMap.get("mockserver")) {
                        profileMap.put("mockserver", "true")
                        String targetUrl = profileMap.get("proxyscheme") + "://" + profileMap.get("proxydomain") + ":" + profileMap.get("proxyport") + '/mock' + profileMap.get("basepath")
                        profileMap.put("targeturl", targetUrl)
                    } else {
                        profileMap.put("mockserver", "false")
                        profileMap.put("targeturl", profileMap.get("targeturl"))
                    }
                }
            } else {
                profiles.put("additional-settings", baseProfile)
            }
        }
        return profiles
    }

    Map<String, Map<String, String>> getSetup() {
        Map<String, Map<String, String>> setup;
        if (StringUtils.isNotBlank(props.getProperty("edgeSetupFile")) && !"\${empty.edgeSetupFile}".equalsIgnoreCase(props.getProperty("edgeSetupFile"))){
            setup = getFromSetupFile()
        } else {
            setup = getFromParameters()
        }
        return setup
    }
}

class Mocker {
    void genMock(File projectDir, File fileSpec) {
        CodegenConfigurator configurator = new CodegenConfigurator()
        configurator.setInputSpec(fileSpec.path)
        configurator.setGenerateAliasAsModel(true)
        configurator.setGeneratorName("nodejs-express-server")
        configurator.setVerbose(false)
        configurator.setLogToStderr(true)
        configurator.setOutputDir(new File(projectDir, "mock/apiproxy/resources/hosted").getAbsolutePath())
        configurator.setValidateSpec(false)
        GlobalSettings.clearProperty(CodegenConstants.MODELS)
        final ClientOptInput input = configurator.toClientOptInput()
        new DefaultGenerator().opts(input).generate()
        File configFile = new File(projectDir, "mock/apiproxy/resources/hosted/config.js")
        String content = IOUtils.toString(new FileInputStream(configFile))
        FileUtils.write(configFile, content.replaceAll("3000,", "process.env.PORT,"))
        File packageFile = new File(projectDir, "mock/apiproxy/resources/hosted/package.json")
        InputStreamReader is = new InputStreamReader(new FileInputStream(packageFile))
        JsonElement packageJson = JsonParser.parseReader(is)
        is.close()
        packageFile.delete()
        packageJson.getAsJsonObject().get("scripts").getAsJsonObject().remove("prestart")
        String json = new GsonBuilder().setPrettyPrinting().create().toJson(packageJson)
        FileWriter writer = new FileWriter(packageFile)
        writer.write(json)
        writer.close()
        Yaml yaml = new Yaml()
        File apiFile = new File(projectDir, "mock/apiproxy/resources/hosted/api/openapi.yaml")
        InputStream inputStream = new FileInputStream(apiFile)
        Map<String, Object> obj = yaml.load(inputStream)
        inputStream.close()

        for (Map server: (List<Map>) obj.get("servers")) {
            URL url = null
            try {
                url = new URL((String) ((Map) server).get("url"));
                String newUrl = url.getProtocol() + "://" + url.getHost();
                server.put("url", newUrl);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        String yml = yaml.dump(obj)
        FileWriter fileWriter = new FileWriter(apiFile)
        fileWriter.write(yml)
        fileWriter.close()
    }
}

final ArchetypeGenerationRequest req = request
final OpenAPI openAPI
final String data
final String specLocation = req.getProperties().get("spec")
final String specAuthName = req.getProperties().get("spec-auth-name")
final String specAuthValue = req.getProperties().get("spec-auth-value")
final String specAuthType = req.getProperties().get("spec-auth-type")

final ProfileSetup profileSetup = new ProfileSetup(req.getProperties())
final Map<String, Map<String, Object>> profiles = profileSetup.getSetup()
final Properties properties = new Properties()
properties.putAll(req.getProperties())
properties.put("setupMap", profiles)
properties.put("profiles", profiles.keySet())
if (profiles.containsKey("additional-settings")) {
    properties.put("additional-settings", profiles.get("additional-settings"))
    profiles.remove("additional-settings")
}

URL specUrl = spec.matches("^(https?|file)://.*") ? new URL(spec) : new File(spec).toURI().toURL()

List<String> keys = new ArrayList<>()
for (String key: properties.keys()) {
    if ('${empty.property}'.equals(properties.get(key))) {
        keys.add(key)
    }
}
for (String key: keys) {
    properties.remove(key)
}

if (specLocation.matches("^https?://.*") && StringUtils.isNotBlank(specAuthType)) {
    AuthorizationValue authorizationValue = new AuthorizationValue(specAuthName, specAuthValue, specAuthType)
    OpenAPIV3Parser parser = new OpenAPIV3ParserData()
    openAPI = parser.readWithInfo(spec, Arrays.asList(authorizationValue)).getOpenAPI()
    data = ( (OpenAPIV3ParserData) parser ).getSpecData()
} else {
    OpenAPIV3Parser parser = new OpenAPIV3ParserData()
    openAPI = parser.readWithInfo(spec, (List)null).getOpenAPI()
    data = ( (OpenAPIV3ParserData) parser ).getSpecData()
}

//Obtem a versão da API no Swagger
String version = openAPI.getInfo().getVersion()
if(StringUtils.isNotBlank(version)){
    properties.put("apiversion", version)
}

//Obter o email pelo Swagger
Contact contact = openAPI.getInfo().getContact()
if (contact != null){
    String email = contact.getEmail()
    if (StringUtils.isNotBlank(email)){
        properties.put("emailDev", email)
    }
}

//Obtem a lista de Paths e metodos
List<APIProxyFlow> flows = new ArrayList<>()
for (String path: openAPI.getPaths().keySet()) {
    for (PathItem.HttpMethod verb: openAPI.getPaths().get(path).readOperationsMap().keySet()) {
        Operation operation = openAPI.getPaths().get(path).readOperationsMap().get(verb)
        APIProxyFlow flow = new APIProxyFlow()
        flow.setName(operation.getOperationId()!=null?operation.getOperationId():"")
        flow.setDesc(operation.getSummary())
        flow.setPath(path)
        flow.setVerb(verb.toString())
        flows.add(flow)
    }
}
properties.put("flows", flows)

File outputDirectory = new File(req.outputDirectory)
File projectDir = new File(outputDirectory, req.artifactId)
File docDir = new File(projectDir, "specs")
docDir.mkdirs()

File specFile = new File(docDir, new File(specUrl.getFile()).getName())
properties.put("specFileName", specFile.getName())
FileWriter fw = new FileWriter(specFile)
fw.write(data)
fw.close()
if (StringUtils.isNotBlank(properties.getProperty("templateDir")) && !"\${empty.templateDir}".equalsIgnoreCase(properties.getProperty("templateDir"))) {
    FileUtils.copyDirectory(new File(properties.getProperty("templateDir")), projectDir)
}

//Tratamento para os arquivos de testes
List<String> verbos = new ArrayList<>()
for(APIProxyFlow flow : flows){
    verbos.add(flow.getVerb())
}

//Remove os arquivos não necessários de testes unitários
File dirUnitTests = new File(projectDir, "tests/unit/features")
File[] files = dirUnitTests.listFiles();
for (File file : files) {
    if (!file.isDirectory()){
        String fverb = file.getName().split("[.]")[0].split("_")[1]
        if(!verbos.contains(fverb)){
            file.delete()
        }
    }
}

//Remove os arquivos não necessários de testes integrados
File dirIntTests = new File(projectDir, "tests/integration/features")
File[] filesInt = dirIntTests.listFiles();
for (File file : filesInt) {
    if (!file.isDirectory()){
        String fverb = file.getName().split("[.]")[0].split("_")[1]
        if(!verbos.contains(fverb)){
            file.delete()
        }
    }
}

System.out.println("---------------DEBUG---------------")
profileSetup.dumpMap(properties, 0)
System.out.println("---------------DEBUG---------------")

//FileUtils.copyFile(specFile, new File(projectDir, "mock/apiproxy/resources/hosted/api/"+specFile.name))
File profileFile = new File(projectDir, "config/profile-env.yaml.vm")
File _envDir = new File(projectDir, "edge/_env")

for (String profile: profiles.keySet()) {
    File targetProfileFile = new File(projectDir, String.format("config/%s.yaml.vm", profile))
    FileUtils.copyFile(profileFile, targetProfileFile)
    File envDir = new File(projectDir, "edge/env/".concat(profile))
    envDir.mkdirs()
    properties.put("profile", profile)
    TemplateReplacer templateReplacer = new TemplateReplacer(outputDirectory, properties)
    templateReplacer.merge(targetProfileFile)
    for (File configFile: _envDir.listFiles()) {
        if (configFile.isFile()) {
            FileUtils.copyFileToDirectory(configFile, envDir)
        }
    }
}

profileFile.delete()
FileUtils.forceDelete(_envDir)

new FileWalker(new RootFilesMover()).walk(new File(projectDir, "root"))
new FileWalker(new TemplateExecutor(outputDirectory, properties)).walk(projectDir)
new FileWalker(new FileRenamer()).walk(projectDir)

boolean buildMock = false
for (String profile: profiles.keySet()) {
    if ("true".equals(profiles.get(profile).get("mockserver"))) {
        buildMock = true
        break
    }
}
if (buildMock) {
    new Mocker().genMock(projectDir, specFile)
} else {
    //FileUtils.forceDelete(new File(projectDir, "mock"))
}
new File(projectDir, "root").delete()
if (!"true".equals(req.getProperties().get("enable-cors"))) {
    new File(projectDir, "apiproxy/policies/" + req.getProperties().get("cors-policy-name") + ".xml").delete()
}
new FileWalker(new XmlFormater()).walk(projectDir)

File filePom = new File(projectDir, "pom.xml.vm")
filePom.delete()
filePom = new File(projectDir, "pom.xml")
filePom.delete()
