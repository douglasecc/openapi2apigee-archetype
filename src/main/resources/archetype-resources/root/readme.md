[![CLaro|GF](http://nexus.prod.k8s.claro.com.br/repository/Markdown-Claro/Markdown/Imagens/LogoClaro.png)](https://claro.com.br)
# Passo a passo para utilização da Esteira Devops no Apigee:
## [GITLAB][git-repo-url]
##### Criar o repositório da sua API baseado no baseado no Template "templateV2_apigee";
[![templateV2_apigee](http://nexus.prod.k8s.claro.com.br/repository/Markdown-Claro/Markdown/Imagens/templateV2-apigeer.png)](http://NET002PRDLNX395.dcing.corp/Gestao_Fontes/templateV2_apigee.git)
----
  - Clicar na opção Grupos/Apigee/Seu Grupo;
  - Clicar em Novo projeto;
  - CLicar na opção de import project;
  - No Project Name inserir o nome da sua API;
 
![templateV2_apigee](http://nexus.prod.k8s.claro.com.br/repository/Markdown-Claro/Markdown/Imagens/create-repo.png) 

- Criar uma "Branch de Trabalho" ou Branch Feacture a partir da master;

![templateV2_apigee](http://nexus.prod.k8s.claro.com.br/repository/Markdown-Claro/Markdown/Imagens/branch-feacture.png)

- Subir on fontes desenvolvidos para a branch criada;
- Efetuar um Merge da sua branch de trabalho inicialmente para a branch lab;

## [SONARQUBE][sonar-url]

![sonarqube](http://nexus.prod.k8s.claro.com.br/repository/Markdown-Claro/Markdown/Imagens/sonarqube.png)

## [VALIDAÇÃO ESTÁTICA][VALPANCHER]

![validapancher](http://nexus.prod.k8s.claro.com.br/repository/Markdown-Claro/Markdown/Imagens/QaApigee.png)

A respeito do [SONARQUBE][sonar-url] e da [VALIDAÇÃO ESTÁTICA][VALPANCHER]:
 -
 - O [SONARQUBE][sonar-url] juntamente a [VALIDAÇÃO ESTÁTICA][VALPANCHER] são executados nas seguintes Branches;
 - A Validação Estática é executata usando o comando "curl", mas também pode utilizar o portal [Integração][VALPANCHER];
 
| Branch | SonarQube | Validação Estática
| ------ | ------ | ------|
| lab | SIM | SIM |
| dev | SIM | SIM |
| test | SIM | SIM |
| master | NÃO | SIM |

## [JENKINS][jenkins-url]

[![Jenkins](http://nexus.prod.k8s.claro.com.br/repository/Markdown-Claro/Markdown/Imagens/Jenkins.png)](http://jenkins.prod.k8s.claro.com.br/login?from=%2F)

- Efetuar login no Jenkins- utilizar o usuário de rede Dominio Claro Subir on fontes desenvolvidos para a branch criada;

  - Criar o JOB no Jenkins, dentro da aba "Apigee" USANDO O MODELO DO jOB DE "Multibranch Pipeline"
  - 
  ![templateV2_apigee](http://nexus.prod.k8s.claro.com.br/repository/Markdown-Claro/Markdown/Imagens/novoJob.png)

  -
![templateV2_apigee](http://nexus.prod.k8s.claro.com.br/repository/Markdown-Claro/Markdown/Imagens/multibranchPipeline.png)

  
  - Alterar os valores da contrução do job baseado nas informações do repositório do Gitlab(url, branch, etc);
  
![templateV2_apigee](http://nexus.prod.k8s.claro.com.br/repository/Markdown-Claro/Markdown/Imagens/configuraJob.png)


  - Efetuar o filtro na opção abaixo e inserir inicialmente : "lab";
  - Mudar o tempo do Scan da multibranch;

![templateV2_apigee](http://nexus.prod.k8s.claro.com.br/repository/Markdown-Claro/Markdown/Imagens/configuraJob2.png)

  - Salvar o job;
 ### Execuções do JOB- Branch- LAB

Alguns pontos a serem observados:
 - 
 - A branch "lab" não efetua validação estática (Valida Pancher) e nem faz teste unitário("mockado") ela serve somente para validar seu desenvolvimento e "passar" p sonar;
 - O job irá rodar todas as branches, considerar sempre inicialmente as branchs "lab", as outras branchs inserir na configuração do job a passo que o desenvolvimento for evoluindo;
 - Verificar se se desenvolvimento está ok baseado no histórico do build;
 - Caso o QualityGate reprovar o Job mostrará também no [Sonarqube][sonar-url], efetuar as devidas correções (sempre efetuar a correção na sua branch de trabalho) e efetuar um novo merge;
 - Caso o Job for executado com sucesso efetuar o merge da branch de trabalho para Dev;
 ---
 ![visao-merge](http://nexus.prod.k8s.claro.com.br/repository/Markdown-Claro/Markdown/Imagens/job5.png)
 
#### Exemplo de uma execução de um Job na branch Lab

- #### Visão do Job
![visao-job1](http://nexus.prod.k8s.claro.com.br/repository/Markdown-Claro/Markdown/Imagens/job1.png)
- #### Visão do Job- modo de Steps e Histórico de Builds
![visao-job2](http://nexus.prod.k8s.claro.com.br/repository/Markdown-Claro/Markdown/Imagens/job2.png)
- #### Visão do Job- modo de [BlueOcean][blue]
![visao-job3](http://nexus.prod.k8s.claro.com.br/repository/Markdown-Claro/Markdown/Imagens/job3.png)

#### Exemplo de análise do [Sonarqube][sonar-url] de um Job
![visao-sonar](http://nexus.prod.k8s.claro.com.br/repository/Markdown-Claro/Markdown/Imagens/job4.png)

### Execuções do JOB- Branch- DEV

Alguns pontos a serem observados:
 -
 - A branch "dev" efetua validação estática [Validação Estática][valpancher], faz teste unitário("mockado") além de "passar" p sonar;
 - Inserir na configuração do job a branch "dev "a passo que o desenvolvimento for evoluindo;
  ![visao-jobdev](http://nexus.prod.k8s.claro.com.br/repository/Markdown-Claro/Markdown/Imagens/job6.png)
 - Esperar o pipeline rodar o ambiente de "dev" automaticamente;
 - Analisar os resultados do deploy e dos testes "mockados";
 - Caso o teste "mockado" der falha, ou seu código não passar pela [Validação Estática][valpancher] por alguma razão efetuar as devidas correções (sempre efetuar a correção na sua branch de trabalho) e efetuar um novo merge;
 ![visao-jobdev2](http://nexus.prod.k8s.claro.com.br/repository/Markdown-Claro/Markdown/Imagens/job7.png)
 ![visao-jobdev2](http://nexus.prod.k8s.claro.com.br/repository/Markdown-Claro/Markdown/Imagens/job8.png)
 - Caso o job "dev" for realizado com sucesso efetuar um merge na sua branch de trabalho para branch de "test" e solicitar ao LT ou responsável pelo projeto a aprovação com o responsável de Testes que o mesmo receberá via email;

### Execuções do JOB- Branch- TEST

Alguns pontos a serem observados:
 -
 - Inserir na configuração do job a branch "test "a passo que o desenvolvimento for evoluindo;
 ![visao-jobdev2](http://nexus.prod.k8s.claro.com.br/repository/Markdown-Claro/Markdown/Imagens/job9.png)
 - O responsável de Testes via jenkins antes de aprovar analisará od Job anteriores se foram executados com sucesso(o job dev), caso o job não foi efetuado deploy e executado com sucesso, reprovar o job e contatar o responsável pelo projeto o motivo da recusa; 
 
#### Exemplo de email enviado para o responsável de Teste para análise:
![visao-jobdev2](http://nexus.prod.k8s.claro.com.br/repository/Markdown-Claro/Markdown/Imagens/email1.png)
 - O responsavel pelo testes caso os jobs anteriores forem executados com sucesso aprovar e aguardar e execuções dos testes;
#### Exemplo de job executado em Test com sucesso :
- #### Visão do Job- Teste
![visao-jobtest](http://nexus.prod.k8s.claro.com.br/repository/Markdown-Claro/Markdown/Imagens/job10.png)
- #### Relatório do teste - [Apickli][apickli]
![visao-jobtest](http://nexus.prod.k8s.claro.com.br/repository/Markdown-Claro/Markdown/Imagens/job11.png)
  - Se o deploy e os testes foram executados com sucesso a api está elegivel por hora a ir para produção;

### Execuções do JOB- Branch- MASTER

Alguns pontos a serem observados:
  - 
  - Inserir na configuração do job a branch "master" a passo que o desenvolvimento for evoluindo;
 ![visao-jobdev2](http://nexus.prod.k8s.claro.com.br/repository/Markdown-Claro/Markdown/Imagens/job12.png)
 - Informar ao LT ou responsável a solicitação do merge da branch de trabalho para a branch "master";
 ![visao-merge1](http://nexus.prod.k8s.claro.com.br/repository/Markdown-Claro/Markdown/Imagens/merge1.png)
- As aprovações para o deploy no branch master passa pelas seguintes áreas: P.O(Project Owner),SIT, Prod e GMUD;
- Alinhar com P.O para efetuar a aprovação;
- Alinhar com Sit para efetuar a aprovação;
- SIT efetuará as devidas análises antes da aprovação ou recusa;

#### Exemplo de envio de email e aprovação para PO(Project Owner):

-![visao-sit1](http://nexus.prod.k8s.claro.com.br/repository/Markdown-Claro/Markdown/Imagens/email1.png)
##### Exemplo de envio de email- em comum para as aprovações.
-![visao-sit1](http://nexus.prod.k8s.claro.com.br/repository/Markdown-Claro/Markdown/Imagens/aprovsit.png)
 - P.O receberá um retorno de email confirmando a aprovação:
 - O Project Owner, através das evidências analisará pelos históricos dos testes se a API é elegível para entrar em produção.
 ![visao-sit1](http://nexus.prod.k8s.claro.com.br/repository/Markdown-Claro/Markdown/Imagens/approvedsit.png)

 - Após a aprovação de P.O a prox aprovação será de SIT;

#### Exemplo de envio de email e aprovação para área de SIT:

-![visao-sit1](http://nexus.prod.k8s.claro.com.br/repository/Markdown-Claro/Markdown/Imagens/emailsit.png)
-![visao-sit1](http://nexus.prod.k8s.claro.com.br/repository/Markdown-Claro/Markdown/Imagens/aprovsit.png)
 - SIT receberá um retorno de email confirmando a aprovação:
 
 ![visao-sit1](http://nexus.prod.k8s.claro.com.br/repository/Markdown-Claro/Markdown/Imagens/approvedsit.png)

 - Após a aprovação de SIT a prox aprovação será de Prod;

#### Exemplo de envio de email e aprovação para área de PROD:
-![visao-sit1](http://nexus.prod.k8s.claro.com.br/repository/Markdown-Claro/Markdown/Imagens/emailprod.png)
 ##### Solicitar para Produção antes de aprovar efetuar as configurações que não são realizados no Pipeline no ambiente do Apigee;
-![visao-sit1](http://nexus.prod.k8s.claro.com.br/repository/Markdown-Claro/Markdown/Imagens/aprovprod.png)
 - PROD receberá um retorno de email confirmando a aprovação:
 
 ![visao-sit1](http://nexus.prod.k8s.claro.com.br/repository/Markdown-Claro/Markdown/Imagens/approvedprod.png)
  - Após as configurações alinhar com GMUD a entrada da api para entrar em produção;

#### Exemplo de envio de email e aprovação para área de GMUD:

-![visao-sit1](http://nexus.prod.k8s.claro.com.br/repository/Markdown-Claro/Markdown/Imagens/emailprod.png)
-![visao-sit1](http://nexus.prod.k8s.claro.com.br/repository/Markdown-Claro/Markdown/Imagens/infogmud.png)
-![visao-sit1](http://nexus.prod.k8s.claro.com.br/repository/Markdown-Claro/Markdown/Imagens/aprovgmud.png)
 - GMUD receberá um retorno de email confirmando a aprovação:
 
 ![visao-sit1](http://nexus.prod.k8s.claro.com.br/repository/Markdown-Claro/Markdown/Imagens/approvedsit.png)

#### Para que GMUD aprove o deply em produção a mesma seguirá os segiuntes passos análises:
 
 -- Gmud analisará as segiuntes infomações de Governança:

| INF da Entrega do Proj. | Valores | 
| ------ | ------ |
| Nome da APi | nome_api-v1 |
| Ambiente | SAAS/ON-PREMISE/AMBOS |
| Criticidade | BAIXA/MÉDIA/ALTA |
| Indisponibilidade | SIM/NÃO |
| Há Integrações ou Dependências | SIM/NÃO |
| Análise Capacity | SIM/NÃO |

 - Caso houver algum dado divergente Gmud efetuará contato com a área responsável;
 - Caso estiver ok, a GMud aprovará a APi para a entrada em produção, a mesma efetuará deploy em um horário agendado definido com o COE e Produção;

### [NEXUS][nexus-url]

![visao-nexus2sit1](http://nexus.prod.k8s.claro.com.br/repository/Markdown-Claro/Markdown/Imagens/nexus2.png)
  - Após toda a etapa de aprovação o Deploy é agendado para um horário definido pelo COE e Produção gerando o artefato e versionando no Nexus o deploy do job realizado:
  
![visao-nexus](http://nexus.prod.k8s.claro.com.br/repository/Markdown-Claro/Markdown/Imagens/nexus.png)

### Obs Gerais:
 - TODO o desenvolvimento, correção, açteração deve-se realizados na branch de trabalho(feacture) da entrega;
 - Em TODAS as etapas, se em algum momento houver alteração todo o processo deverá exeturar desde o início(lab, dev, test), independente da alteração que realizou, salvo exceções pontuais analisadas caso a caso.(Ex, criticidade da api, o Desenvolvedor informou que é baixa mas é alta,) pode fazer correção e efetuar um merge direto para master, como informativas, que não impacta na mudança do fonte da API(informações de GMUD), se for alterar alguma informação relacionado ao ambiente, todo o processo deverá ser realizado desde o início (lab, dev, test e master).

# CONTATO PARA SUPORTE/ DÚVIDAS OU PROBLEMAS:


| Área. | Contato | 
| ------ | ------ |
| Gestão de Fontes | gestao.fontes@claro.com.br |
| Gerencia de Mudanças Claro | gerencia.net@claro.com.br |
| Produção | PROD.WEB_SP1@claro.com.br |
| COE - Integração Claro | definir-email@claro.com.br |

## Release notes:
### Version 1.0
- Versão inicial do documento

### Version 1.1
- Inclusão do step de aprovação para a equipe de TESTES(Lider de Torre)
- Exclusão das análises para aprovação de GMUD

### Version 1.2
- Alteração do Jenkinsfile para inclusão dos emails dos responsáveis

### Version 1.3

- Conjunto de Shared Libs para o projeto do Apigee.

- Descrição dos Arquivos:
    Apickli - Roda os Testes unitários e Integrados;
    Apigee - Classe com métodos para deploy (utilizando maven e sharedPom);
    Aprovation - Classe para descrever aprovações e "guardar" todas as aprovações da pipeline;
    BranchConfiguration - Classe com configuração das branches para o ambiente Claro - Utilizada apenas pelo "toString()"
    commonJenkinsfile - Orquestrador principal da pipeline, faz setup da configuração para deploy e chama sharedlibs;
    Deploy - Classe com descrição do deploy para input no influx e Lista dos Deploys realizados;
    DeployConfiguration - Classe com a configuração do deploy corrente como branch, commit hash, e parâmetros recebidos;
    EnvironmentConfiguration - Classe com as configurações  do ambientes, como endereçõs de servidores, descrição das equipes e etc... ;
    SharedPom - Step de 'git clone' dos shared poms que contém a goals para deploy no apigee (as confis do repositório ficam em EnvironmentConfiguration)
    SonarScanner - Cria o projeto no SonarQube, associa à métrica correta e roda a análise;
    SwaggerValidate - Executa o "Valida Pancher" na API
    Utils - Funções auxiliares, sem pertencer à uma classe com métodos especializados;
    
- Outros Arquivos:
    JenkinsFile - Arquivo de Exemplo com os parâmetros aceitos pela pipeline;
    sonar-project.properties - Arquivo de exemplo para as propriedades do sonar;

### Version 1.4

- Aletaração do aprovação para produçao (de Lider de Torre para o P.O).

[![CLaro|GF](http://nexus.prod.k8s.claro.com.br/repository/Markdown-Claro/Markdown/Imagens/LogoClaro.png)](https://claro.com.br)

[//]: # (These are reference links used in the body of this note and get stripped out when the markdown processor does its job. There is no need to format nicely because it shouldn't be seen. Thanks SO - http://stackoverflow.com/questions/4823468/store-comments-in-markdown-syntax)
   [git-repo-url]: <http://10.29.2.32>
   [jenkins-url]: <http://jenkins.prod.k8s.claro.com.br>
   [sonar-url]: <http://sonar.prod.k8s.claro.com.br/>
   [nexus-url]: <http://nexus.prod.k8s.claro.com.br>
   [valpancher]: <http://10.29.236.102/qa/>
   [blue]: <https://www.jenkins.io/projects/blueocean/>
   [apickli]: <https://github.com/apickli/apickli>
   
