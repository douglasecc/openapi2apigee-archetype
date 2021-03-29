try{ 
    var erro = response.content.asJSON;
    var resp = {};
 
    resp.apiVersion = context.getVariable('apiVersion') || "1"; 
    resp.transactionId = context.getVariable('transactionId'); 
    resp.error = erro;
 
    response.content = JSON.stringify(resp); 
} catch (e) { 
    context.setVariable('customfault.name', 'ResponseTransform'); 
    throw e; 
} 
