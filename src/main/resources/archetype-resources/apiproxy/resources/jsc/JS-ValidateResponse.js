var faultstringvalue = null;
if (context.getVariable('isParametroInvalidoFault')) {
    context.setVariable('response.status.code', '400');

} else if (context.getVariable('isTechnicalFault')) {
    context.setVariable('response.status.code', '500');

} else if (context.getVariable('isSuccessResponse')) {
    context.setVariable('response.status.code', '200');
}