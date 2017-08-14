package com.phonegap.plugins.barcodescanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import java.util.Date;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import br.com.daruma.framework.mobile.DarumaMobile;
import android.util.Log;
import java.text.NumberFormat;

public class BarcodeScanner extends CordovaPlugin {
    public static final int REQUEST_CODE = 0x0ba7c0de;
	private static final String SCAN = "scan";
    private static final String ENCODE = "encode";
    private static final String CANCELLED = "cancelled";
    private static final String FORMAT = "format";
    private static final String TEXT = "text";
    private static final String LIV = "liv";
    String RETORNOLIVE;
    String VALORLIVE;
    String VALORLIVEDENTROJSON;
    String PRODUTO;
    String EMPRESA_DESCRICAO;
    String CNPJ;
    String INSCR_ESTADUAL;
    String LOGRADOURO;
    String NUMERO;
    String BAIRRO;
    String MUNICIPIO;
    String UF;
    String TELEFONE;
    String MENSAGEM;
    String USUARIO;
    String CLIENTE;
    String NUM_CUPOM;
    String LIVE_DATA;
    String LIVE_HORA;
    String LIVE_ITENS_SEQUENCIA;
    String LIVE_ITENS_PRODUTOID;
    String LIVE_ITENS_DESCRICAO;
    String LIVE_ITENS_QUANTIDADE;
    String LIVE_ITENS_UNIDADE;
    String LIVE_ITENS_VLR_UNIT;
    String LIVE_ITENS_VLR_TOTAL;
    String LIVE_TOTAL_ITENS;
    String LIVE_PAGAMENTO_DESC;
    String LIVE_PAGAMENTO_ACRES;
    String LIVE_PAGAMENTO_MOEDA;
    String LIVE_PAGAMENTO_RECEBIDO;
    String LIVE_PAGAMENTO_TROCO;
    String ITENS;
    String VENDA_ITENS;
    String VENDA_ITENS_BLUETOOTH;
    String VENDA_PAGAMENTO;
    String TIPO;
    String MESA;
    String TOTAL_ITENS;
    String OCUPANTE;
    String VENDA_ID;
    String PRODUTO_ID;
    String QUANTIDADE;
    String TIPO_PDV;
    String LIVE_RAZAO_SOCIAL;
    String CONSUMIDOR;
    String ACRES_DESC;
    String VLR_RECEBER;
    String URL_CONSULTA;
    String CHAVE_ACESSO;
    String PROTOCOLO;
    String SERIE;
    String DATA_HORA_AUTORIZACAO;
    String LIVE_TOTAL_TRIBUTOS;
    String LIVE_QRCODE;
    String LIVE_OBSERVACAO;
    String LIVE_ESTABELECIMENTO;
    String COMANDA;
    String TESTE1;
    String TESTE2;
    String TESTE3;
    String IP;
    String PORTA;
    String IMPRESSAO_TIPO;
    String BLOCO;
       
    private static final String DATA = "data";
    private static final String TYPE = "type";
    private static final String SCAN_INTENT = "com.google.zxing.client.android.SCAN";
    private static final String ENCODE_DATA = "ENCODE_DATA";
    private static final String ENCODE_TYPE = "ENCODE_TYPE";
    private static final String ENCODE_INTENT = "com.phonegap.plugins.barcodescanner.ENCODE";
    private static final String TEXT_TYPE = "TEXT_TYPE";
    private static final String EMAIL_TYPE = "EMAIL_TYPE";
    private static final String PHONE_TYPE = "PHONE_TYPE";
    private static final String SMS_TYPE = "SMS_TYPE";

    private static final String LOG_TAG = "BarcodeScanner";

    private CallbackContext callbackContext;

    /**
     * Constructor.
     */
    
    public BarcodeScanner() {
    }

    /**
     * Executes the request.
     *
     * This method is called from the WebView thread. To do a non-trivial amount of work, use:
     *     cordova.getThreadPool().execute(runnable);
     *
     * To run on the UI thread, use:
     *     cordova.getActivity().runOnUiThread(runnable);
     *
     * @param action          The action to execute.
     * @param args            The exec() arguments.
     * @param callbackContext The callback context used when calling back into JavaScript.
     * @return                Whether the action was valid.
     *
     * @sa https://github.com/apache/cordova-android/blob/master/framework/src/org/apache/cordova/CordovaPlugin.java
     */
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) {
        this.callbackContext = callbackContext;
        

        if (action.equals(ENCODE)) {
            JSONObject obj = args.optJSONObject(0);
            live(args);
            if (obj != null) {
                String type = obj.optString(TYPE);
                String data = obj.optString(DATA);

                // If the type is null then force the type to text
                if (type == null) {
                    type = TEXT_TYPE;
                }

                if (data == null) {
                    callbackContext.error("User did not specify data to encode");
                    return true;
                }

                encode(type, data);
            } else {
                callbackContext.error("User did not specify data to encode");
                return true;
            }
        } else if (action.equals(SCAN)) {
            //scan(args);
			live(args);
        } else {
            return false;
        }
        return true;
    }

    /**
     * Starts an intent to scan and decode a barcode.
     */
    public void scan(JSONArray args) {
        Intent intentScan = new Intent(SCAN_INTENT);
        intentScan.addCategory(Intent.CATEGORY_DEFAULT);
        // add config as intent extras
        if(args.length() > 0) {
            JSONObject obj;
            JSONArray names;
            String key;
            Object value;

            for(int i=0; i<args.length(); i++) {

                try {
                    obj = args.getJSONObject(i);
                } catch(JSONException e) {
                    Log.i("CordovaLog", e.getLocalizedMessage());
                    continue;
                }

                names = obj.names();
                for(int j=0; j<names.length(); j++) {
                    try {
                        key = names.getString(j);
                        value = obj.get(key);

                        if(value instanceof Integer) {
                            intentScan.putExtra(key, (Integer)value);
                        } else if(value instanceof String) {
                            intentScan.putExtra(key, (String)value);
                        }

                    } catch(JSONException e) {
                        Log.i("CordovaLog", e.getLocalizedMessage());
                        continue;
                    }
                }
            }

        }

        // avoid calling other phonegap apps
        intentScan.setPackage(this.cordova.getActivity().getApplicationContext().getPackageName());

        this.cordova.startActivityForResult((CordovaPlugin) this, intentScan, REQUEST_CODE);
    }

    /**
     * Called when the barcode scanner intent completes.
     *
     * @param requestCode The request code originally supplied to startActivityForResult(),
     *                       allowing you to identify who this result came from.
     * @param resultCode  The integer result code returned by the child activity through its setResult().
     * @param intent      An Intent, which can return result data to the caller (various data can be attached to Intent "extras").
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                JSONObject obj = new JSONObject();
                try {
                    obj.put(TEXT, intent.getStringExtra("SCAN_RESULT"));
                    obj.put(LIV, "JOSE VALDOMIRO");
                    obj.put(FORMAT, intent.getStringExtra("SCAN_RESULT_FORMAT"));
                    obj.put(CANCELLED, false);
                } catch (JSONException e) {
                    Log.d(LOG_TAG, "This should never happen");
                }
                //this.success(new PluginResult(PluginResult.Status.OK, obj), this.callback);
                this.callbackContext.success(obj);
            } else if (resultCode == Activity.RESULT_CANCELED) {
                JSONObject obj = new JSONObject();
                try {
                    obj.put(TEXT, "wwwwwwwwwwwwww");
                    obj.put(FORMAT, "");
                    obj.put(CANCELLED, true);
                } catch (JSONException e) {
                    Log.d(LOG_TAG, "This should never happen");
                }
                //this.success(new PluginResult(PluginResult.Status.OK, obj), this.callback);
                this.callbackContext.success(obj);
            } else {
                //this.error(new PluginResult(PluginResult.Status.ERROR), this.callback);
                this.callbackContext.error("Unexpected error");
            }
        }
    }

    /**
     * Initiates a barcode encode.
     *
     * @param type Endoiding type.
     * @param data The data to encode in the bar code.
     */
    public void encode(String type, String data) {
        Intent intentEncode = new Intent(ENCODE_INTENT);
        intentEncode.putExtra(ENCODE_TYPE, type);
        intentEncode.putExtra(ENCODE_DATA, data);
        // avoid calling other phonegap apps
        intentEncode.setPackage(this.cordova.getActivity().getApplicationContext().getPackageName());

        this.cordova.getActivity().startActivity(intentEncode);
    }
    //Função para impressão do QrCode
    public String montarQrCode(String dados, String tCorrecao, String lModulo)
    {
	String qrcode="";
	char[] correcao = new char[1];
	char[] modulo = new char[1];
	char[] bMSB = new char[1];
	char[] bLSB = new char[1];
	
	correcao=tCorrecao.toCharArray();
	modulo[0]=(char) Integer.parseInt(lModulo);
	
	//Calcula byte mais e menos significativo somando 2 ao tamanho da string
	
	//Calculo do byte mais significativo
	//Se o valor do calculo for igual a 0, então colocamos o inteiro 0. Se colocar como NULL direto da erro
	
	if(((dados.length() + 2)>>8) == 0)
	{
		bMSB[0]=0;
	}
	else
	{
		bMSB[0]=(char)((dados.length() + 2)>>8);
	}
	
	//Calculo do byte menos significativo
	//Se o valor do calculo for igual a 0, então colocamos o inteiro 0. Se colocar como NULL direto da erro
	if(((dados.length() + 2) & 0x00ff) ==0)
	{
		bLSB[0]=0;
	}
	else
	{
		bLSB[0]=(char)((dados.length() + 2) & 0x00ff);
	}
	//Monta o comando de impressao do Qrcode adicionando os bytes [ESC]<129> no inicio
	//Comando de qrcode de impressora: [ESC] <129> <–Size><+Size> <Width> <Ecc> <D001> <D002> . . . <Dnnn>
	qrcode=""+((char)0x1B) +""+((char)0x81)+bLSB[0]+bMSB[0]+modulo[0]+correcao[0]+dados;
	return qrcode;
	
	}	

public void live(JSONArray args){
        VENDA_PAGAMENTO="";
        VENDA_ITENS="";
            
          // add config as intent extras
        if(args.length() > 0) {
            JSONObject obj2;
            JSONObject obj3;
            
            JSONArray itens;
            JSONArray moeda;
            String key;
            Object value;
            for(int i=0; i<args.length(); i++) {

                try {
                     
                    obj2 = args.getJSONObject(i);
                    //JSONArray subArray = subObject.getJSONArray("sub1");
                    JSONArray object = obj2.getJSONArray("venda");
                    
                    LIVE_ESTABELECIMENTO = object.getJSONObject(i).getString("tipo_estabelecimento").toString();    
                    LIVE_RAZAO_SOCIAL = object.getJSONObject(i).getString("razao_social").toString();
                    EMPRESA_DESCRICAO = object.getJSONObject(i).getString("empresa").toString();
                    CNPJ = object.getJSONObject(i).getString("live_cnpj").toString();
                    TIPO = object.getJSONObject(i).getString("tipo").toString();
                    PRODUTO_ID = object.getJSONObject(i).getString("produto_id").toString();
                    MESA = object.getJSONObject(i).getString("mesa").toString();
                    INSCR_ESTADUAL = object.getJSONObject(i).getString("live_inscr_estadual").toString();
                    LOGRADOURO = object.getJSONObject(i).getString("endereco").toString();
                    NUMERO = object.getJSONObject(i).getString("numero").toString();
                    BAIRRO = object.getJSONObject(i).getString("bairro").toString();
                    MUNICIPIO = object.getJSONObject(i).getString("municipio").toString();
                    UF = object.getJSONObject(i).getString("uf").toString();
                    TELEFONE = object.getJSONObject(i).getString("fone").toString();
                    MENSAGEM = object.getJSONObject(i).getString("mensagem").toString();
                    USUARIO = object.getJSONObject(i).getString("usuario").toString();
                    NUM_CUPOM = object.getJSONObject(i).getString("num_cupom").toString();
                    VENDA_ID = object.getJSONObject(i).getString("venda_id").toString();
                    LIVE_DATA = object.getJSONObject(i).getString("data").toString();
                    LIVE_HORA = object.getJSONObject(i).getString("hora").toString();
                    TIPO_PDV = object.getJSONObject(i).getString("tipo_pdv").toString();
                    CHAVE_ACESSO = object.getJSONObject(i).getString("live_chave_acesso").toString();
                    PROTOCOLO = object.getJSONObject(i).getString("live_protocolo").toString();
                    SERIE = object.getJSONObject(i).getString("live_serie").toString();
                    DATA_HORA_AUTORIZACAO = object.getJSONObject(i).getString("live_data_hora").toString();
                    LIVE_TOTAL_TRIBUTOS = object.getJSONObject(i).getString("live_tributos").toString();
                    LIVE_QRCODE = object.getJSONObject(i).getString("live_qrcode").toString();
                    URL_CONSULTA = object.getJSONObject(i).getString("live_url_consulta").toString();
                    IMPRESSAO_TIPO = object.getJSONObject(i).getString("live_tipo_impressao").toString();
                    IP = object.getJSONObject(i).getString("impressora_ip").toString();
                    PORTA = object.getJSONObject(i).getString("impressora_porta").toString();
                     
                    COMANDA="";
                    if(LIVE_ESTABELECIMENTO.equals("Restaurante")){
                        
                    COMANDA+=((char) 0x1B)+"a0"+ "Comanda: " + PRODUTO_ID + ((char) 0x0A);
                    }else{
                    COMANDA="";    
                    }
                    
                    itens = object.getJSONObject(i).getJSONArray("itens");
                    moeda = object.getJSONObject(i).getJSONArray("pagamentos");
                    int SEQ=1;
                    for(int y=0; y<itens.length(); y++)
                    {
			         
                    //TESTE2+="\n"+itens.getJSONObject(y).getString("produto_desc").toString();
                    CLIENTE = itens.getJSONObject(y).getString("live_cliente").toString();
                    CONSUMIDOR = itens.getJSONObject(y).getString("live_consumidor").toString();    
                 //   LIVE_ITENS_SEQUENCIA = SEQ; //itens.getJSONObject(y).getString("sequencia").toString();
                    LIVE_ITENS_PRODUTOID = itens.getJSONObject(y).getString("produto_id").toString();
                    LIVE_ITENS_DESCRICAO = itens.getJSONObject(y).getString("produto_desc").toString();
                    LIVE_ITENS_QUANTIDADE = itens.getJSONObject(y).getString("produto_quant").toString();
                    LIVE_ITENS_UNIDADE = itens.getJSONObject(y).getString("produto_unidade").toString();
                    LIVE_ITENS_VLR_UNIT = itens.getJSONObject(y).getString("produto_vlr_unit").toString();
                    LIVE_ITENS_VLR_TOTAL = itens.getJSONObject(y).getString("produto_vlr_total").toString();
                    TOTAL_ITENS = itens.getJSONObject(y).getString("total_itens").toString();
                    OCUPANTE = itens.getJSONObject(y).getString("vlr_ocupante").toString();
                    QUANTIDADE = itens.getJSONObject(y).getString("quan_itens").toString();
                    LIVE_OBSERVACAO = itens.getJSONObject(y).getString("live_observacao").toString();    
                       
                    //+ ((char) 0x1B) + "" + ((char) 0x6A) + "0" + String.format("%03d", SEQ)    
                    VENDA_ITENS+= ((char) 0x1B)+"a0"+ LIVE_ITENS_PRODUTOID 
                    +((char) 0x20) +((char) 0x20) 
                    + LIVE_ITENS_DESCRICAO 
                    + ((char) 0x0A)
                    + ((char) 0x1B)+"a2" 
                    + "" + LIVE_ITENS_QUANTIDADE 
                    + "" + ((char) 0x20) + "" + ((char) 0x20) 
                    + "" + LIVE_ITENS_UNIDADE + ((char) 0x20) + ((char) 0x20) 
                    + "" + LIVE_ITENS_VLR_UNIT + ((char) 0x20) + ((char) 0x20) + "   " + LIVE_ITENS_VLR_TOTAL 
                    + ((char) 0x0A);
                    SEQ++;
                    };
                   
                    
                    for(int x=0; x<moeda.length(); x++)
                    {
                        
                    //TESTE3+="\n"+moeda.getJSONObject(x).getString("pag_moeda").toString();
                    LIVE_TOTAL_ITENS = moeda.getJSONObject(x).getString("live_itens_vlr_total").toString();    
                    LIVE_PAGAMENTO_DESC = moeda.getJSONObject(x).getString("pag_desconto").toString();
                    LIVE_PAGAMENTO_ACRES = moeda.getJSONObject(x).getString("pag_acrescimo").toString();
                    LIVE_PAGAMENTO_MOEDA = moeda.getJSONObject(x).getString("pag_moeda").toString();
                    LIVE_PAGAMENTO_RECEBIDO = moeda.getJSONObject(x).getString("pag_recebido").toString();
                    VLR_RECEBER = moeda.getJSONObject(x).getString("vlr_apagar").toString();    
                    LIVE_PAGAMENTO_TROCO = moeda.getJSONObject(x).getString("pag_troco").toString();
                      
                   VENDA_PAGAMENTO+= ((char) 0x1B)+"a0"+ LIVE_PAGAMENTO_MOEDA +"                         "+ LIVE_PAGAMENTO_RECEBIDO
                    + ((char) 0x0A);
                                    
                    };
                    ACRES_DESC="";
                    
                    if(Float.parseFloat(LIVE_PAGAMENTO_DESC) > 0 ){
                    
                         ACRES_DESC+= "Acréscimos/Desconto R$" +"                   " + ((char) 0x1B) + "" + ((char) 0x6A) + "0" + LIVE_PAGAMENTO_DESC + ((char) 0x0A);
                        
                    }else if(Float.parseFloat(LIVE_PAGAMENTO_ACRES) > 0 ){
                    
                         ACRES_DESC+= "Acréscimos/Desconto R$" +"                   " + ((char) 0x1B) + "" + ((char) 0x6A) + "0" + LIVE_PAGAMENTO_ACRES + ((char) 0x0A);
                    
                    }else{
                         ACRES_DESC="" + ((char) 0x0A);
                    }   
                    
                    
                } catch(JSONException e) {
                    Log.i("CordovaLog", e.getLocalizedMessage());
                    continue;
                }

            }
                    }

    if(IMPRESSAO_TIPO.equals("Rede")){
        
    if(TIPO.equals("CANC")){
            		// Pega a hora
Date dat= new Date();
//http://www.technotalkative.com/android-json-parsing/

        char[] chrRetorno = new char[1164];
    	DarumaMobile objeto = DarumaMobile
				.inicializar("@SOCKET(HOST="+ IP +";PORT=" +PORTA +")");
		objeto.confParametros("@FRAMEWORK(TRATAEXCECAO=TRUE)");
		//TextView versao = (TextView) findViewById(R.id.textView1);
		//versao.setText(String.valueOf(obj.retornaVersao()));
    
			objeto.iniciarComunicacao();
			

			objeto.enviarComando("" + ((char) 0x1B) + "@" + ((char) 0x1B)
					+ "j1" + ((char) 0x1B) + "" + ((char) 0x45) + "" + ((char) 0x07)
					+ ((char) 0x0E) + "" + ((char) 0x14) + EMPRESA_DESCRICAO + ((char) 0x1B) + "" + ((char) 0x46)
                    + ((char) 0x0A)
                    + ((char) 0x0A)
                    + ((char) 0x13) + "CNPJ"             
                    + ((char) 0x3A)             
                    + ((char) 0x13) + CNPJ
                    + ((char) 0x20)             
                    + ((char) 0x12) + "IE"
                    + ((char) 0x3A)             
                    + ((char) 0x12) + INSCR_ESTADUAL            
                    + ((char) 0x0A)
                    + ((char) 0x12) + LOGRADOURO
                    + ((char) 0x2C)
                    + ((char) 0x20)             
                    + ((char) 0x12) + NUMERO
                    + ((char) 0x20)             
                    + ((char) 0x2D)             
                    + ((char) 0x12) + BAIRRO
                    + ((char) 0x20)             
                    + ((char) 0x2D)
                    + ((char) 0x20)             
                    + ((char) 0x12) + MUNICIPIO
                    + ((char) 0x20)             
                    + ((char) 0x2D)
                    + ((char) 0x20)             
                    + ((char) 0x12) + UF
                    + ((char) 0x20)             
                    + ((char) 0x2D)
                    + ((char) 0x20)
                    + ((char) 0x12) + "FONE"
                    + ((char) 0x3A)             
                    + ((char) 0x12) + TELEFONE
                    + ((char) 0x0A) + "" + ((char) 0x0A)             
                    + ((char) 0x1B) + "" + ((char) 0x45) + "------------------------------------------------" + ((char) 0x1B) + "" + ((char) 0x46)
                    + ((char) 0x0A) + "" + ((char) 0x0A)
                    + ((char) 0x1B) + "" + ((char) 0x6A) + "1" + "NÚMERO"
                    + ((char) 0x3A)
                    + ((char) 0x12) + "" + ((char) 0x0B) + "" + NUM_CUPOM             
                    + ((char) 0x13) + " " + ((char) 0x09) + "DATA"
                    + ((char) 0x3A)
                    + ((char) 0x12) + "" + ((char) 0x0B) + "" + LIVE_DATA
                    + ((char) 0x20)             
                    + ((char) 0x12) + "" + ((char) 0x0B) + "" + LIVE_HORA + ((char) 0x20) + ((char) 0x20)
                    + ((char) 0x0A) + "" + ((char) 0x0A)             
                    + ((char) 0x1B) + "" + ((char) 0x45) + "PEDIDO CANCELADO" + ((char) 0x1B) + "" + ((char) 0x46)            
					+ ((char) 0x0A) + "" + ((char) 0x0A)
                    + "------------------------------------------------"
                    + ((char) 0x0A)             
                    + ((char) 0x1B) + "" + ((char) 0x45) + "Código" + ((char) 0x1B) + "" + ((char) 0x6A) + "0" + ((char) 0x20) + ((char) 0x1B) + "" + ((char) 0x46)
                    + ((char) 0x1B) + "" + ((char) 0x45) + "Descrição" + ((char) 0x20) + ((char) 0x20) + ((char) 0x20) + ((char) 0x20) + ((char) 0x20) + ((char) 0x20) + ((char) 0x1B) + "" + ((char) 0x46)          
                    + ((char) 0x1B) + "" + ((char) 0x45) + "Qtde UN" + ((char) 0x20) + ((char) 0x20) + ((char) 0x1B) + "" + ((char) 0x46)
                    + ((char) 0x1B) + "" + ((char) 0x45) + "Vl Unit" + ((char) 0x20) + ((char) 0x20) + ((char) 0x1B) + "" + ((char) 0x46)
                    + ((char) 0x1B) + "" + ((char) 0x45) + "Vl Total" + ((char) 0x1B) + "" + ((char) 0x46)          
                    + ((char) 0x0A)
                    + "------------------------------------------------"
                    + ((char) 0x0A)
                    + ((char) 0x12) + VENDA_ITENS             
                    + ((char) 0x1B) + "" + ((char) 0x45) + "________________________________________________" + ((char) 0x1B) + "" + ((char) 0x46)            
                    + ((char) 0x0A) + "" + ((char) 0x0A)
                    + "Qtde. total de itens" + ((char) 0x1B) + "" + ((char) 0x6A) + "0"
                    + "                        " + QUANTIDADE
                    + ((char) 0x0A)             
                    + "Valor total R$" + ((char) 0x1B) + "" + ((char) 0x6A) + "0"            
                    + "                           " + LIVE_TOTAL_ITENS
                    + ((char) 0x0A)            
                    + "Desconto R$" + ((char) 0x1B) + "" + ((char) 0x6A) + "0"            
                    + "                              " + LIVE_PAGAMENTO_DESC
                    + ((char) 0x0A)
                    + "Acrescimo R$" + ((char) 0x1B) + "" + ((char) 0x6A) + "0"            
                    + "                             " + LIVE_PAGAMENTO_ACRES
                    + ((char) 0x0A)             
                    + ((char) 0x1B) + "" + ((char) 0x45) + "================================================" + ((char) 0x1B) + "" + ((char) 0x46)
                    + ((char) 0x0A)             
                    + ((char) 0x0A) + "" + ((char) 0x0A)
                    + ((char) 0x1B) + "" + ((char) 0x6A) + "0" + "FORMA DE PAGAMENTO                 VALOR PAGO R$"
                    + ((char) 0x0A)             
                    + ((char) 0x13) + VENDA_PAGAMENTO
                    + ((char) 0x0A)             
                    + ((char) 0x13) + "TROCO R$" + "                                 " + LIVE_PAGAMENTO_TROCO
                    + ((char) 0x0A) + "" + ((char) 0x0A)             
                    + ((char) 0x1B) + "" + ((char) 0x6A) + "1" + "CLIENTE"
                    + ((char) 0x3A)
                    + ((char) 0x12) + "" + ((char) 0x0B) + "" + CLIENTE             
                    + ((char) 0x0A) + "" + ((char) 0x0A) + "" + ((char) 0x0A)             
                    + ((char) 0x1B) + "" + ((char) 0x45) + "NÃO CONTÉM VALOR FISCAL!" + ((char) 0x1B) + "" + ((char) 0x46) 
                    + ((char) 0x0A) + "" + ((char) 0x0A)             
                    + ((char) 0x0E) + "" + ((char) 0x14) + MENSAGEM            
                    + ((char) 0x0A) + "" + ((char) 0x0A)
                    + ((char) 0x1B) + "" + ((char) 0x6A) + "0" + "OPERADOR"
                    + ((char) 0x3A)             
                    + ((char) 0x12) + "" + ((char) 0x0B) + "" + USUARIO
                    + ((char) 0x0A)
                    + ((char) 0x1B) + "" + ((char) 0x6A) + "0" + COMANDA
                    + ((char) 0x0A) + "" + ((char) 0x0A)                          
                    + ((char) 0x09)
					+ ((char) 0x1B) + "" + ((char) 0x6A) + "2" + ((char) 0x1B) + "" + ((char) 0x45) + "www.livesistemas.com" + "" + ((char) 0x1B) + "" + ((char) 0x46) + ((char) 0x0A)             
                    + ((char) 0x0A) + "" + ((char) 0x0A) + "" + ((char) 0x0A) + "" + ((char) 0x0A) + "" + ((char) 0x0A) + "" + ((char) 0x0A)
                    + ((char) 0x1B) + "" + ((char) 0x6d));             
                    //+ ((char) 0x0A));
          objeto.fecharComunicacao();
            
        }
    
    if(TIPO.equals("I")){
            		// Pega a hora
Date dat= new Date();
//http://www.technotalkative.com/android-json-parsing/

        char[] chrRetorno = new char[1164];
    	DarumaMobile objeto = DarumaMobile
				.inicializar("@SOCKET(HOST="+ IP +";PORT=" +PORTA +")");
		objeto.confParametros("@FRAMEWORK(TRATAEXCECAO=TRUE)");
		//TextView versao = (TextView) findViewById(R.id.textView1);
		//versao.setText(String.valueOf(obj.retornaVersao()));
    
			objeto.iniciarComunicacao();
			

			objeto.enviarComando("" + ((char) 0x1B) + "@" + ((char) 0x1B)
					+ "j1" + ((char) 0x1B) + "" + ((char) 0x45) + "" + ((char) 0x07)
					+ ((char) 0x0E) + "" + ((char) 0x14) + EMPRESA_DESCRICAO + ((char) 0x1B) + "" + ((char) 0x46)
                    + ((char) 0x0A)
                    + ((char) 0x0A)
                    + ((char) 0x13) + "CNPJ"             
                    + ((char) 0x3A)             
                    + ((char) 0x13) + CNPJ
                    + ((char) 0x20)             
                    + ((char) 0x12) + "IE"
                    + ((char) 0x3A)             
                    + ((char) 0x12) + INSCR_ESTADUAL            
                    + ((char) 0x0A)
                    + ((char) 0x12) + LOGRADOURO
                    + ((char) 0x2C)
                    + ((char) 0x20)             
                    + ((char) 0x12) + NUMERO
                    + ((char) 0x20)             
                    + ((char) 0x2D)             
                    + ((char) 0x12) + BAIRRO
                    + ((char) 0x20)             
                    + ((char) 0x2D)
                    + ((char) 0x20)             
                    + ((char) 0x12) + MUNICIPIO
                    + ((char) 0x20)             
                    + ((char) 0x2D)
                    + ((char) 0x20)             
                    + ((char) 0x12) + UF
                    + ((char) 0x20)             
                    + ((char) 0x2D)
                    + ((char) 0x20)
                    + ((char) 0x12) + "FONE"
                    + ((char) 0x3A)             
                    + ((char) 0x12) + TELEFONE
                    + ((char) 0x0A) + "" + ((char) 0x0A)             
                    + ((char) 0x1B) + "" + ((char) 0x45) + "________________________________________________" + ((char) 0x1B) + "" + ((char) 0x46)
                    + ((char) 0x0A) + "" + ((char) 0x0A)
                    + ((char) 0x1B) + "" + ((char) 0x6A) + "1" + "MESA:" + MESA + ((char) 0x20) + ((char) 0x20)
                    + "DATA: " + LIVE_DATA + ((char) 0x20) + LIVE_HORA
                    + ((char) 0x0A) + "" + ((char) 0x0A)
                    + ((char) 0x1B) + "" + ((char) 0x6A) + "1" + ((char) 0x1B) + "" + ((char) 0x45) + "COMANDA: " + PRODUTO_ID + ((char) 0x1B) + "" + ((char) 0x46)               
					+ ((char) 0x0A) + "" + ((char) 0x0A)             
                    + ((char) 0x1B) + "" + ((char) 0x6A) + "0" + "DESCRIÇÃO: " + LIVE_ITENS_DESCRICAO            
                    + ((char) 0x0A)             
                    + ((char) 0x1B) + "" + ((char) 0x6A) + "0" + "OBSERVAÇÃO: " + LIVE_OBSERVACAO                         
                    + ((char) 0x0A) + "" + ((char) 0x0A)
                    + ((char) 0x1B) + "" + ((char) 0x6A) + "0" + "QUANTIDADE: " + LIVE_ITENS_QUANTIDADE
                    + ((char) 0x0A) + "" + ((char) 0x0A)             
                    + ((char) 0x1B) + "" + ((char) 0x45) + "================================================" + ((char) 0x1B) + "" + ((char) 0x46)                         
                    + ((char) 0x0A) + "" + ((char) 0x0A)                         
                    + ((char) 0x1B) + "" + ((char) 0x6A) + "0" + "OPERADOR"
                    + ((char) 0x3A)             
                    + ((char) 0x12) + "" + ((char) 0x0B) + "" + USUARIO
                    + ((char) 0x0A) + "" + ((char) 0x0A)             
                    + ((char) 0x09)
					+ ((char) 0x1B) + "" + ((char) 0x6A) + "2" + ((char) 0x1B) + "" + ((char) 0x45) + "www.livesistemas.com" + "" + ((char) 0x1B) + "" + ((char) 0x46) + ((char) 0x0A)             
                    + ((char) 0x0A) + "" + ((char) 0x0A) + "" + ((char) 0x0A) + "" + ((char) 0x0A)
                    + ((char) 0x1B) + "" + ((char) 0x6d));             
                    //+ ((char) 0x0A));
          objeto.fecharComunicacao();
    }
    
    if(TIPO.equals("V") && TIPO_PDV.equals("Nao Fiscal")){
            		// Pega a hora
Date dat= new Date();
//http://www.technotalkative.com/android-json-parsing/

        char[] chrRetorno = new char[1164];
    	DarumaMobile objeto = DarumaMobile
				//.inicializar("@SOCKET(HOST="+ IP +";PORT=" +PORTA +")");
                //.inicializar("@BLUETOOTH(ADDRESS="+ IP +")");
                  .inicializar("@SOCKET(HOST="+ IP +";PORT=" +PORTA +")");
		objeto.confParametros("@FRAMEWORK(TRATAEXCECAO=TRUE)");
		//TextView versao = (TextView) findViewById(R.id.textView1);
		//versao.setText(String.valueOf(obj.retornaVersao()));
    
			objeto.iniciarComunicacao();
			

			objeto.enviarComando("" + ((char) 0x1B) + "@" + ((char) 0x1B)
					+ "j1" + ((char) 0x1B) + "" + ((char) 0x45) + "" + ((char) 0x07)
					+ ((char) 0x0E) + "" + ((char) 0x14) + EMPRESA_DESCRICAO + ((char) 0x1B) + "" + ((char) 0x46)
                    + ((char) 0x0A)
                    + ((char) 0x0A)
                    + ((char) 0x13) + "CNPJ"             
                    + ((char) 0x3A)             
                    + ((char) 0x13) + CNPJ
                    + ((char) 0x20)             
                    + ((char) 0x12) + "IE"
                    + ((char) 0x3A)             
                    + ((char) 0x12) + INSCR_ESTADUAL            
                    + ((char) 0x0A)
                    + ((char) 0x12) + LOGRADOURO
                    + ((char) 0x2C)
                    + ((char) 0x20)             
                    + ((char) 0x12) + NUMERO
                    + ((char) 0x20)             
                    + ((char) 0x2D)             
                    + ((char) 0x12) + BAIRRO
                    + ((char) 0x20)             
                    + ((char) 0x2D)
                    + ((char) 0x20)             
                    + ((char) 0x12) + MUNICIPIO
                    + ((char) 0x20)             
                    + ((char) 0x2D)
                    + ((char) 0x20)             
                    + ((char) 0x12) + UF
                    + ((char) 0x20)             
                    + ((char) 0x2D)
                    + ((char) 0x20)
                    + ((char) 0x12) + "FONE"
                    + ((char) 0x3A)             
                    + ((char) 0x12) + TELEFONE
                    + ((char) 0x0A) + "" + ((char) 0x0A)             
                    + ((char) 0x1B) + "" + ((char) 0x45) + "------------------------------------------------" + ((char) 0x1B) + "" + ((char) 0x46)
                    + ((char) 0x0A) + "" + ((char) 0x0A)
                    + ((char) 0x1B) + "" + ((char) 0x6A) + "1" + "NÚMERO" + ((char) 0x3A) + NUM_CUPOM
                    + ((char) 0x20) + ((char) 0x20) + "DATA" + ((char) 0x3A) + LIVE_DATA
                    + ((char) 0x20)             
                    + LIVE_HORA
                    + ((char) 0x0A) + "" + ((char) 0x0A)             
                    + ((char) 0x1B) + "" + ((char) 0x45) + "PEDIDO" + ((char) 0x1B) + "" + ((char) 0x46)            
					+ ((char) 0x0A) + "" + ((char) 0x0A)
                    + "------------------------------------------------"
                    + ((char) 0x0A)             
                    + ((char) 0x1B) + "" + ((char) 0x45) + "Código" + ((char) 0x1B) + "" + ((char) 0x6A) + "0" + ((char) 0x20) + ((char) 0x1B) + "" + ((char) 0x46)
                    + ((char) 0x1B) + "" + ((char) 0x45) + "Descrição" + ((char) 0x20) + ((char) 0x20) + ((char) 0x20) + ((char) 0x20) + ((char) 0x20) + ((char) 0x20) + ((char) 0x1B) + "" + ((char) 0x46)          
                    + ((char) 0x1B) + "" + ((char) 0x45) + "Qtde UN" + ((char) 0x20) + ((char) 0x20) + ((char) 0x1B) + "" + ((char) 0x46)
                    + ((char) 0x1B) + "" + ((char) 0x45) + "Vl Unit" + ((char) 0x20) + ((char) 0x20) + ((char) 0x1B) + "" + ((char) 0x46)
                    + ((char) 0x1B) + "" + ((char) 0x45) + "Vl Total" + ((char) 0x1B) + "" + ((char) 0x46)          
                    + ((char) 0x0A)
                    + "------------------------------------------------"
                    + ((char) 0x0A)             
                    + ((char) 0x12) + VENDA_ITENS             
                    + ((char) 0x1B) + "" + ((char) 0x45) + "________________________________________________" + ((char) 0x1B) + "" + ((char) 0x46)            
                    + ((char) 0x0A) + "" + ((char) 0x0A)
                    + "Qtde. total de itens" + ((char) 0x1B) + "" + ((char) 0x6A) + "0"
                    + "                        " + QUANTIDADE
                    + ((char) 0x0A)             
                    + "Valor total R$" + ((char) 0x1B) + "" + ((char) 0x6A) + "0"            
                    + "                           " + LIVE_TOTAL_ITENS
                    + ((char) 0x0A)            
                    + "Desconto R$" + ((char) 0x1B) + "" + ((char) 0x6A) + "0"            
                    + "                              " + LIVE_PAGAMENTO_DESC
                    + ((char) 0x0A)
                    + "Acrescimo R$" + ((char) 0x1B) + "" + ((char) 0x6A) + "0"            
                    + "                             " + LIVE_PAGAMENTO_ACRES             
                    + ((char) 0x0A) + "" + ((char) 0x0A)
                    + ((char) 0x1B) + "" + ((char) 0x6A) + "0" + "FORMA DE PAGAMENTO                 VALOR PAGO R$"
                    + ((char) 0x0A)             
                    + ((char) 0x13) + VENDA_PAGAMENTO
                    + ((char) 0x0A)             
                    + ((char) 0x13) + "TROCO R$" + "                                 " + LIVE_PAGAMENTO_TROCO   
                    + ((char) 0x0A) + "" + ((char) 0x0A)             
                    + ((char) 0x1B) + "" + ((char) 0x6A) + "1" + "CLIENTE "
                    + ((char) 0x12) + "" + ((char) 0x0B) + "" + CLIENTE             
                    + ((char) 0x0A) + "" + ((char) 0x0A) + "" + ((char) 0x0A)             
                    + ((char) 0x1B) + "" + ((char) 0x45) + "NÃO CONTÉM VALOR FISCAL!" + ((char) 0x1B) + "" + ((char) 0x46) 
                    + ((char) 0x0A) + "" + ((char) 0x0A)             
                    + ((char) 0x0E) + "" + ((char) 0x14) + MENSAGEM            
                    + ((char) 0x0A) + "" + ((char) 0x0A)
                    + ((char) 0x1B) + "" + ((char) 0x6A) + "0" + "OPERADOR"
                    + ((char) 0x3A)             
                    + ((char) 0x12) + "" + ((char) 0x0B) + "" + USUARIO
                    + ((char) 0x0A)             
                    + ((char) 0x1B) + "" + ((char) 0x6A) + "0" + COMANDA             
                    + ((char) 0x0A) + "" + ((char) 0x0A)             
                    + ((char) 0x09)
					+ ((char) 0x1B) + "" + ((char) 0x6A) + "2" + ((char) 0x1B) + "" + ((char) 0x45) + "www.livesistemas.com" + "" + ((char) 0x1B) + "" + ((char) 0x46) + ((char) 0x0A)             
                    + ((char) 0x0A) + "" + ((char) 0x0A) + "" + ((char) 0x0A) + "" + ((char) 0x0A) + "" + ((char) 0x0A) + "" + ((char) 0x0A)
                    + ((char) 0x1B) + "" + ((char) 0x6d));             
                    //+ ((char) 0x0A));
          //objeto.fecharComunicacao();
            
        }
    
    if(TIPO.equals("V") && TIPO_PDV.equals("Fiscal")){
            		// Pega a hora
Date dat= new Date();
//http://www.technotalkative.com/android-json-parsing/

        char[] chrRetorno = new char[1164];
    	DarumaMobile objeto = DarumaMobile
				.inicializar("@SOCKET(HOST="+ IP +";PORT=" +PORTA +")");
		objeto.confParametros("@FRAMEWORK(TRATAEXCECAO=TRUE)");
		//TextView versao = (TextView) findViewById(R.id.textView1);
		//versao.setText(String.valueOf(obj.retornaVersao()));
    
			objeto.iniciarComunicacao();
			

			objeto.enviarComando("" + ((char) 0x1B) + "@" + ((char) 0x1B)
					+ "j1" + ((char) 0x1B) + "" + ((char) 0x45) + ((char) 0x07)
                    + ((char) 0x0E) + "" + ((char) 0x14) + EMPRESA_DESCRICAO + ((char) 0x1B) + "" + ((char) 0x46)
                    + ((char) 0x0A) + ((char) 0x0A)             
                    + ((char) 0x13) + "CNPJ" + ((char) 0x1B) + "" + ((char) 0x6A) + "1"             
                    + ((char) 0x3A)
                    + ((char) 0x13) + CNPJ + ((char) 0x20)
                    + ((char) 0x13) + "IE" + ((char) 0x1B) + "" + ((char) 0x6A) + "1"             
                    + ((char) 0x3A)
                    + ((char) 0x13) + INSCR_ESTADUAL
                    + ((char) 0x0A)             
					+ ((char) 0x0E) + "" + ((char) 0x14) + ((char) 0x1B) + "" + ((char) 0x45) + LIVE_RAZAO_SOCIAL + ((char) 0x1B) + "" + ((char) 0x46)              
                    + ((char) 0x0A)
                    + ((char) 0x12) + LOGRADOURO
                    + ((char) 0x2C)
                    + ((char) 0x20)             
                    + ((char) 0x12) + NUMERO
                    + ((char) 0x20)             
                    + ((char) 0x2D)             
                    + ((char) 0x12) + BAIRRO
                    + ((char) 0x20)             
                    + ((char) 0x2D)
                    + ((char) 0x20)             
                    + ((char) 0x12) + MUNICIPIO
                    + ((char) 0x20)             
                    + ((char) 0x2D)
                    + ((char) 0x20)             
                    + ((char) 0x12) + UF
                    + ((char) 0x20)             
                    + ((char) 0x2D)
                    + ((char) 0x20)
                    + ((char) 0x12) + "FONE"
                    + ((char) 0x3A)             
                    + ((char) 0x12) + TELEFONE
                    + ((char) 0x0A) + "" + ((char) 0x0A)             
                    + "------------------------------------------------" 
                    + ((char) 0x0A)
                    + ((char) 0x1B) + "" + ((char) 0x45) + "Documento Auxiliar da Nota Fiscal de Consumidor Eletrônica" + ((char) 0x1B) + "" + ((char) 0x46) + ((char) 0x1B) + "" + ((char) 0x6A) + "1"  + ((char) 0x0A)           
                    + "------------------------------------------------"            
                    + ((char) 0x0A)            
                    + ((char) 0x1B) + "" + ((char) 0x45) + "Código" + ((char) 0x1B) + "" + ((char) 0x6A) + "0" + ((char) 0x20) + ((char) 0x1B) + "" + ((char) 0x46)
                    + ((char) 0x1B) + "" + ((char) 0x45) + "Descrição" + ((char) 0x20) + ((char) 0x20) + ((char) 0x20) + ((char) 0x20) + ((char) 0x20) + ((char) 0x20) + ((char) 0x1B) + "" + ((char) 0x46)          
                    + ((char) 0x1B) + "" + ((char) 0x45) + "Qtde UN" + ((char) 0x20) + ((char) 0x20) + ((char) 0x1B) + "" + ((char) 0x46)
                    + ((char) 0x1B) + "" + ((char) 0x45) + "Vl Unit" + ((char) 0x20) + ((char) 0x20) + ((char) 0x1B) + "" + ((char) 0x46)
                    + ((char) 0x1B) + "" + ((char) 0x45) + "Vl Total" + ((char) 0x1B) + "" + ((char) 0x46)          
                    + ((char) 0x0A)
                    + ((char) 0x12) + VENDA_ITENS             
                    + ((char) 0x1B) + "" + ((char) 0x45) + "________________________________________________" + ((char) 0x1B) + "" + ((char) 0x46)            
                    + ((char) 0x0A) + "" + ((char) 0x0A)
                    + "Qtde. total de itens" + ((char) 0x1B) + "" + ((char) 0x6A) + "0"
                    + "                        " + QUANTIDADE             
                    + ((char) 0x0A)             
                    + "Valor total R$" + ((char) 0x1B) + "" + ((char) 0x6A) + "0"            
                    + "                           " + LIVE_TOTAL_ITENS
                    + ((char) 0x0A)             
                    + ACRES_DESC             
                    + ((char) 0x1B) + "" + ((char) 0x45) + "Valor a Pagar R$                         " + VLR_RECEBER + ((char) 0x1B) + "" + ((char) 0x46) + ((char) 0x1B) + "" + ((char) 0x6A) + "1"  
                    + ((char) 0x0A)
                    + ((char) 0x1B) + "" + ((char) 0x6A) + "0" + "FORMA DE PAGAMENTO                 VALOR PAGO R$"
                    + ((char) 0x0A)             
                    + ((char) 0x13) + VENDA_PAGAMENTO
                    + ((char) 0x0A)
                    + ((char) 0x13) + "TROCO R$" + "                                 " + LIVE_PAGAMENTO_TROCO   
                    + ((char) 0x0A) + ((char) 0x0A)             
                    + ((char) 0x1B) + "" + ((char) 0x6A) + "1" + ((char) 0x1B) + "" + ((char) 0x45) + "Consulte pela Chave de Acesso em" + ((char) 0x1B) + "" + ((char) 0x46)
                    + ((char) 0x0A)             
                    + ((char) 0x1B) + "" + ((char) 0x6A) + "1" + URL_CONSULTA
                    + ((char) 0x0A)             
                    + ((char) 0x1B) + "" + ((char) 0x6A) + "1" + CHAVE_ACESSO          
                    + ((char) 0x0A) + "" + ((char) 0x0A)
                    + ((char) 0x1B) + "" + ((char) 0x45) + CONSUMIDOR + "" + ((char) 0x1B) + "" + ((char) 0x46) + CLIENTE + ((char) 0x1B) + "" + ((char) 0x6A) + "1"                                     
                    + ((char) 0x0A) + "" + ((char) 0x0A)
                    + ((char) 0x1B) + "" + ((char) 0x45) + "NFC-e nº " + NUM_CUPOM + ((char) 0x20) + "Serie " + SERIE + ((char) 0x20) + LIVE_DATA + ((char) 0x20) + LIVE_HORA + ((char) 0x1B) + "" + ((char) 0x46) + ((char) 0x1B) + "" + ((char) 0x6A) + "1"  + ((char) 0x0A)
                    + ((char) 0x1B) + "" + ((char) 0x45) + "Protocolo de autorização:" + ((char) 0x1B) + "" + ((char) 0x46) +  PROTOCOLO + ((char) 0x1B) + "" + ((char) 0x6A) + "1"  + ((char) 0x0A)
                    + ((char) 0x1B) + "" + ((char) 0x45) + "Data da autorização" + ((char) 0x1B) + "" + ((char) 0x46) + ((char) 0x20) + DATA_HORA_AUTORIZACAO + ((char) 0x1B) + "" + ((char) 0x6A) + "1"  + ((char) 0x0A) + ((char) 0x0A)
                    + ((char) 0x1B) + "" + ((char) 0x6A) + "1" + "EMITIDA EM AMBIENTE DE HOMOLOGAÇÃO - SEM VALOR   FISCAL"
                    + ((char) 0x0A) + ((char) 0x0A)             
                    + montarQrCode(LIVE_QRCODE, "H", "4")
                    + ((char) 0x0A)             
                    + ((char) 0x0E) + "" + ((char) 0x14) + MENSAGEM            
                    + ((char) 0x0A)
                    + ((char) 0x1B) + "" + ((char) 0x6A) + "1" + "Tributos Totais Incidentes (Lei Federal 12.741/2012) R$: " + LIVE_TOTAL_TRIBUTOS
                    + ((char) 0x0A) + ((char) 0x0A)             
                    + ((char) 0x1B) + "" + ((char) 0x6A) + "0" + "Operador"
                    + ((char) 0x3A)             
                    + ((char) 0x12) + "" + ((char) 0x0B) + "" + USUARIO
                    + ((char) 0x0A)             
                    + ((char) 0x1B) + "" + ((char) 0x6A) + "0" + COMANDA             
                    + ((char) 0x0A) + "" + ((char) 0x0A)             
                    + ((char) 0x09)
					+ ((char) 0x1B) + "" + ((char) 0x6A) + "2" + ((char) 0x1B) + "" + ((char) 0x45) + "www.livesistemas.com" + "" + ((char) 0x1B) + "" + ((char) 0x46) + ((char) 0x0A)             
                    + ((char) 0x0A) + "" + ((char) 0x0A) + "" + ((char) 0x0A) + "" + ((char) 0x0A)
                    + ((char) 0x1B) + "" + ((char) 0x6d));             
                    //+ ((char) 0x0A));
          objeto.fecharComunicacao();
            
        }
        
  if(TIPO.equals("C")){        
            		// Pega a hora
Date dat= new Date();
//http://www.technotalkative.com/android-json-parsing/

        char[] chrRetorno = new char[1164];
    	DarumaMobile objeto = DarumaMobile
				.inicializar("@SOCKET(HOST="+ IP +";PORT=" +PORTA +")");
		objeto.confParametros("@FRAMEWORK(TRATAEXCECAO=TRUE)");
		//TextView versao = (TextView) findViewById(R.id.textView1);
		//versao.setText(String.valueOf(obj.retornaVersao()));
    
			objeto.iniciarComunicacao();
			

			objeto.enviarComando("" + ((char) 0x1B) + "@" + ((char) 0x1B)
					+ "j1" + ((char) 0x1B) + "" + ((char) 0x45) + "" + ((char) 0x07)
					+ ((char) 0x0E) + "" + ((char) 0x14) + EMPRESA_DESCRICAO + ((char) 0x1B) + "" + ((char) 0x46)
                    + ((char) 0x0A)
                    + ((char) 0x0A)
                    + ((char) 0x13) + "CNPJ"             
                    + ((char) 0x3A)             
                    + ((char) 0x13) + CNPJ
                    + ((char) 0x20)             
                    + ((char) 0x12) + "IE"
                    + ((char) 0x3A)             
                    + ((char) 0x12) + INSCR_ESTADUAL            
                    + ((char) 0x0A)
                    + ((char) 0x12) + LOGRADOURO
                    + ((char) 0x2C)
                    + ((char) 0x20)             
                    + ((char) 0x12) + NUMERO
                    + ((char) 0x20)             
                    + ((char) 0x2D)             
                    + ((char) 0x12) + BAIRRO
                    + ((char) 0x20)             
                    + ((char) 0x2D)
                    + ((char) 0x20)             
                    + ((char) 0x12) + MUNICIPIO
                    + ((char) 0x20)             
                    + ((char) 0x2D)
                    + ((char) 0x20)             
                    + ((char) 0x12) + UF
                    + ((char) 0x20)             
                    + ((char) 0x2D)
                    + ((char) 0x20)
                    + ((char) 0x12) + "FONE"
                    + ((char) 0x3A)             
                    + ((char) 0x12) + TELEFONE
                    + ((char) 0x0A) + "" + ((char) 0x0A)             
                    + ((char) 0x1B) + "" + ((char) 0x45) + "------------------------------------------------" + ((char) 0x1B) + "" + ((char) 0x46)
                    + ((char) 0x0A) + "" + ((char) 0x0A)
                    + ((char) 0x1B) + "" + ((char) 0x6A) + "1" + "MESA"
                    + ((char) 0x3A) + ((char) 0x20) + MESA             
                    + ((char) 0x20) + ((char) 0x20) + "DATA" + ((char) 0x3A) + LIVE_DATA
                    + ((char) 0x20) + LIVE_HORA + ((char) 0x20) + ((char) 0x20)
                    + ((char) 0x0A) + "" + ((char) 0x0A)             
                    + ((char) 0x1B) + "" + ((char) 0x45) + "CONFERÊNCIA DE MESA" + ((char) 0x1B) + "" + ((char) 0x46)  
					+ ((char) 0x0A) + "" + ((char) 0x0A)
                    + "------------------------------------------------"
                    + ((char) 0x0A)             
                    + ((char) 0x1B) + "" + ((char) 0x45) + "Código" + ((char) 0x1B) + "" + ((char) 0x6A) + "0" + ((char) 0x20) + ((char) 0x1B) + "" + ((char) 0x46)
                    + ((char) 0x1B) + "" + ((char) 0x45) + "Descrição" + ((char) 0x20) + ((char) 0x20) + ((char) 0x20) + ((char) 0x20) + ((char) 0x20) + ((char) 0x20) + ((char) 0x1B) + "" + ((char) 0x46)          
                    + ((char) 0x1B) + "" + ((char) 0x45) + "Qtde UN" + ((char) 0x20) + ((char) 0x20) + ((char) 0x1B) + "" + ((char) 0x46)
                    + ((char) 0x1B) + "" + ((char) 0x45) + "Vl Unit" + ((char) 0x20) + ((char) 0x20) + ((char) 0x1B) + "" + ((char) 0x46)
                    + ((char) 0x1B) + "" + ((char) 0x45) + "Vl Total" + ((char) 0x1B) + "" + ((char) 0x46)          
                    + ((char) 0x0A)
                    + "------------------------------------------------"             
                    + ((char) 0x0A)
                    + ((char) 0x12) + VENDA_ITENS             
                    + ((char) 0x1B) + "" + ((char) 0x45) + "________________________________________________" + ((char) 0x1B) + "" + ((char) 0x46)            
                    + ((char) 0x0A) + "" + ((char) 0x0A)               
                    + "subtotal - R$" + ((char) 0x1B) + "" + ((char) 0x6A) + "0"
                    + ((char) 0x3A)            
                    + "                            " + TOTAL_ITENS
                    + ((char) 0x0A)
                    + "Total por ocupante - R$" + ((char) 0x1B) + "" + ((char) 0x6A) + "0"
                    + ((char) 0x3A)            
                    + "                  " + OCUPANTE             
                    + ((char) 0x0A) + "" + ((char) 0x0A)             
                    + ((char) 0x1B) + "" + ((char) 0x6A) + "1" + "CLIENTE "
                    + ((char) 0x12) + "" + ((char) 0x0B) + "" + CLIENTE             
                    + ((char) 0x0A) + "" + ((char) 0x0A) + "" + ((char) 0x0A)             
                    + ((char) 0x1B) + "" + ((char) 0x45) + "NÃO CONTÉM VALOR FISCAL!" + ((char) 0x1B) + "" + ((char) 0x46) 
                    + ((char) 0x0A) + "" + ((char) 0x0A)             
                    + ((char) 0x0E) + "" + ((char) 0x14) + MENSAGEM             
                    + ((char) 0x0A) + "" + ((char) 0x0A)
                    + ((char) 0x1B) + "" + ((char) 0x6A) + "0" + "OPERADOR"
                    + ((char) 0x3A)             
                    + ((char) 0x12) + "" + ((char) 0x0B) + "" + USUARIO
                    + ((char) 0x0A) + "" + ((char) 0x0A)             
                    + ((char) 0x09)
					+ ((char) 0x1B) + "" + ((char) 0x6A) + "2" + ((char) 0x1B) + "" + ((char) 0x45) + "www.livesistemas.com" + "" + ((char) 0x1B) + "" + ((char) 0x46) + ((char) 0x0A)             
                    + ((char) 0x0A) + "" + ((char) 0x0A) + "" + ((char) 0x0A) + "" + ((char) 0x0A) + "" + ((char) 0x0A) + "" + ((char) 0x0A)             
                    + ((char) 0x0A));
          objeto.fecharComunicacao();
  
  }
}
    
    if(IMPRESSAO_TIPO.equals("Bluetooth")){
        if(TIPO.equals("CANC")){
            		// Pega a hora
Date dat= new Date();
//http://www.technotalkative.com/android-json-parsing/

        char[] chrRetorno = new char[1164];
    	DarumaMobile objeto = DarumaMobile
				//.inicializar("@SOCKET(HOST="+ IP +";PORT=" +PORTA +")");
                    //.inicializar("@BLUETOOTH(ADDRESS="+ IP +")");
                    .inicializar("@BLUETOOTH(ADDRESS="+IP+";TIMEOUT=30)");
		objeto.confParametros("@FRAMEWORK(TRATAEXCECAO=TRUE)");
		//TextView versao = (TextView) findViewById(R.id.textView1);
		//versao.setText(String.valueOf(obj.retornaVersao()));
    
			objeto.iniciarComunicacao();
			

			objeto.enviarComando(+ ((char) 0x1B) + "@"
                    + ((char) 0x0A)
                    + ((char) 0x1B)+"a1" + EMPRESA_DESCRICAO
                    + ((char) 0x0A) + ((char) 0x0A)
                    + ((char) 0x13) + "CNPJ"             
                    + ((char) 0x3A)             
                    + ((char) 0x13) + CNPJ
                    + ((char) 0x20)             
                    + ((char) 0x12) + "IE"
                    + ((char) 0x3A)             
                    + ((char) 0x12) + INSCR_ESTADUAL            
                    + ((char) 0x0A)
                    + ((char) 0x12) + LOGRADOURO
                    + ((char) 0x2C)
                    + ((char) 0x20)             
                    + ((char) 0x12) + NUMERO
                    + ((char) 0x20)             
                    + ((char) 0x2D)             
                    + ((char) 0x12) + BAIRRO
                    + ((char) 0x20)             
                    + ((char) 0x2D)
                    + ((char) 0x20)             
                    + ((char) 0x12) + MUNICIPIO
                    + ((char) 0x20)             
                    + ((char) 0x2D)
                    + ((char) 0x20)             
                    + ((char) 0x12) + UF
                    + ((char) 0x20)             
                    + ((char) 0x2D)
                    + ((char) 0x20)
                    + ((char) 0x12) + "FONE"
                    + ((char) 0x3A)             
                    + ((char) 0x12) + TELEFONE
                    + ((char) 0x0A) + "" + ((char) 0x0A)             
                    + ((char) 0x1B) + "" + ((char) 0x45) + "------------------------------------------------" + ((char) 0x1B) + "" + ((char) 0x46)
                    + ((char) 0x0A) + "" + ((char) 0x0A)
                    + "NUMERO:" + NUM_CUPOM             
                    + ((char) 0x13) + ((char) 0x20) + ((char) 0x20) + ((char) 0x09) + "DATA:" + LIVE_DATA
                    + ((char) 0x20)             
                    + LIVE_HORA + ((char) 0x20) + ((char) 0x20)
                    + ((char) 0x0A) + ((char) 0x0A)             
                    + ((char) 0x1B) +"a1" + ((char) 0x1B) + ((char) 0x45) + "PPEDIDO CANCELADO" + ((char) 0x1B) + "" + ((char) 0x46)            
					+ ((char) 0x0A) + ((char) 0x0A)
                    + "------------------------------------------------"
                    + ((char) 0x0A)             
                    + "Codigo" + ((char) 0x20)
                    + ((char) 0x1B) + "" + ((char) 0x45) + "DDescricao" + ((char) 0x20) + ((char) 0x20) + ((char) 0x20) + ((char) 0x20) + ((char) 0x20) + ((char) 0x20) + ((char) 0x1B) + "" + ((char) 0x46)          
                    + "Qtde UN" + ((char) 0x20) + ((char) 0x20)
                    + ((char) 0x1B) + "" + ((char) 0x45) + "VVl Unit" + ((char) 0x20) + ((char) 0x20) + ((char) 0x1B) + "" + ((char) 0x46)
                    + ((char) 0x1B) + "" + ((char) 0x45) + "VVl Total" + ((char) 0x1B) + "" + ((char) 0x46)          
                    + ((char) 0x0A)
                    + "------------------------------------------------"
                    + ((char) 0x0A)
                    + ((char) 0x12) + VENDA_ITENS             
                    + ((char) 0x1B) + "" + ((char) 0x45) + "________________________________________________" + ((char) 0x1B) + "" + ((char) 0x46)            
                    + ((char) 0x0A) + "" + ((char) 0x0A)
                    + ((char) 0x1B)+"a0" + "Qtde. total de itens:"
                    + "                       " + QUANTIDADE
                    + ((char) 0x0A)             
                    + "Valor total R$:"            
                    + "                          " + LIVE_TOTAL_ITENS
                    + ((char) 0x0A)            
                    + "Desconto R$:"         
                    + "                             " + LIVE_PAGAMENTO_DESC
                    + ((char) 0x0A)
                    + "Acrescimo R$:"           
                    + "                            " + LIVE_PAGAMENTO_ACRES
                    + ((char) 0x0A)             
                    + ((char) 0x1B) + "" + ((char) 0x45) + "================================================" + ((char) 0x1B) + "" + ((char) 0x46)
                    + ((char) 0x0A)
                    + "FORMA DE PAGAMENTO                 VALOR PAGO R$"
                    + ((char) 0x0A)             
                    + ((char) 0x13) + VENDA_PAGAMENTO
                    + ((char) 0x0A)             
                    + ((char) 0x13) + "TROCO R$:" + "                                " + LIVE_PAGAMENTO_TROCO
                    + ((char) 0x0A) + "" + ((char) 0x0A)             
                    + "CLIENTE: "
                    + ((char) 0x1B)+"a1" + CLIENTE              
                    + ((char) 0x0A) + "" + ((char) 0x0A)            
                    + ((char) 0x1B)+"a1" + "NAO CONTEM VALOR FISCAL!" 
                    + ((char) 0x0A) + ((char) 0x0A)            
                    + ((char) 0x1B) +"a1" + MENSAGEM     
                    + ((char) 0x0A) + "" + ((char) 0x0A)
                    + ((char) 0x1B)+"a0" + "OPERADOR:" + "" + USUARIO
                    + ((char) 0x0A)             
                    + COMANDA
                    + ((char) 0x0A) + "" + ((char) 0x0A)                          
					+ ((char) 0x1B)+"a1"+  ((char) 0x1B) + ((char) 0x45) + "wwww.livesistemas.com" + ((char) 0x1B) + ((char) 0x46) + ((char) 0x0A)
                    + ((char) 0x0A) + ((char) 0x0A) + ((char) 0x0A) + ((char) 0x0A)
                    + ((char) 0x1B) + "" + ((char) 0x6d));             
                    //+ ((char) 0x0A));
          //objeto.fecharComunicacao();
            
        }
    
    if(TIPO.equals("I")){
            		// Pega a hora
Date dat= new Date();
//http://www.technotalkative.com/android-json-parsing/

        char[] chrRetorno = new char[1164];
    	DarumaMobile objeto = DarumaMobile
				//.inicializar("@SOCKET(HOST="+ IP +";PORT=" +PORTA +")");
                    //.inicializar("@BLUETOOTH(ADDRESS="+ IP +")");
                    .inicializar("@BLUETOOTH(ADDRESS="+IP+";TIMEOUT=30)");
		objeto.confParametros("@FRAMEWORK(TRATAEXCECAO=TRUE)");
		//TextView versao = (TextView) findViewById(R.id.textView1);
		//versao.setText(String.valueOf(obj.retornaVersao()));
    
			objeto.iniciarComunicacao();
			

			objeto.enviarComando("" + ((char) 0x1B) + "@" + ((char) 0x1B)
					+ "j1" + ((char) 0x1B) + "" + ((char) 0x45) + "" + ((char) 0x07)
					+ ((char) 0x0E) + "" + ((char) 0x14) + EMPRESA_DESCRICAO + ((char) 0x1B) + "" + ((char) 0x46)
                    + ((char) 0x0A)
                    + ((char) 0x0A)
                    + ((char) 0x13) + "CNPJ"             
                    + ((char) 0x3A)             
                    + ((char) 0x13) + CNPJ
                    + ((char) 0x20)             
                    + ((char) 0x12) + "IE"
                    + ((char) 0x3A)             
                    + ((char) 0x12) + INSCR_ESTADUAL            
                    + ((char) 0x0A)
                    + ((char) 0x12) + LOGRADOURO
                    + ((char) 0x2C)
                    + ((char) 0x20)             
                    + ((char) 0x12) + NUMERO
                    + ((char) 0x20)             
                    + ((char) 0x2D)             
                    + ((char) 0x12) + BAIRRO
                    + ((char) 0x20)             
                    + ((char) 0x2D)
                    + ((char) 0x20)             
                    + ((char) 0x12) + MUNICIPIO
                    + ((char) 0x20)             
                    + ((char) 0x2D)
                    + ((char) 0x20)             
                    + ((char) 0x12) + UF
                    + ((char) 0x20)             
                    + ((char) 0x2D)
                    + ((char) 0x20)
                    + ((char) 0x12) + "FONE"
                    + ((char) 0x3A)             
                    + ((char) 0x12) + TELEFONE
                    + ((char) 0x0A) + "" + ((char) 0x0A)             
                    + ((char) 0x1B) + "" + ((char) 0x45) + "________________________________________________" + ((char) 0x1B) + "" + ((char) 0x46)
                    + ((char) 0x0A) + "" + ((char) 0x0A)
                    + ((char) 0x1B) + "" + ((char) 0x6A) + "1" + "MESA:" + MESA + ((char) 0x20) + ((char) 0x20)
                    + "DATA: " + LIVE_DATA + ((char) 0x20) + LIVE_HORA
                    + ((char) 0x0A) + "" + ((char) 0x0A)
                    + ((char) 0x1B) + "" + ((char) 0x6A) + "1" + ((char) 0x1B) + "" + ((char) 0x45) + "COMANDA: " + PRODUTO_ID + ((char) 0x1B) + "" + ((char) 0x46)               
					+ ((char) 0x0A) + "" + ((char) 0x0A)             
                    + ((char) 0x1B) + "" + ((char) 0x6A) + "0" + "DESCRIÇÃO: " + LIVE_ITENS_DESCRICAO            
                    + ((char) 0x0A)             
                    + ((char) 0x1B) + "" + ((char) 0x6A) + "0" + "OBSERVAÇÃO: " + LIVE_OBSERVACAO                         
                    + ((char) 0x0A) + "" + ((char) 0x0A)
                    + ((char) 0x1B) + "" + ((char) 0x6A) + "0" + "QUANTIDADE: " + LIVE_ITENS_QUANTIDADE
                    + ((char) 0x0A) + "" + ((char) 0x0A)             
                    + ((char) 0x1B) + "" + ((char) 0x45) + "================================================" + ((char) 0x1B) + "" + ((char) 0x46)
                    + ((char) 0x0A) + "" + ((char) 0x0A)                         
                    + ((char) 0x1B) + "" + ((char) 0x6A) + "0" + "OPERADOR"
                    + ((char) 0x3A)             
                    + ((char) 0x12) + "" + ((char) 0x0B) + "" + USUARIO
                    + ((char) 0x0A) + "" + ((char) 0x0A)             
                    + ((char) 0x09)
					+ ((char) 0x1B) + "" + ((char) 0x6A) + "2" + ((char) 0x1B) + "" + ((char) 0x45) + "www.livesistemas.com" + "" + ((char) 0x1B) + "" + ((char) 0x46) + ((char) 0x0A)             
                    + ((char) 0x0A) + "" + ((char) 0x0A) + "" + ((char) 0x0A) + "" + ((char) 0x0A)
                    + ((char) 0x1B) + "" + ((char) 0x6d));             
                    //+ ((char) 0x0A));
          //objeto.fecharComunicacao();
    }
    
    if(TIPO.equals("V") && TIPO_PDV.equals("Nao Fiscal")){
            		// Pega a hora
Date dat= new Date();
//http://www.technotalkative.com/android-json-parsing/

        char[] chrRetorno = new char[1164];
    	DarumaMobile objeto = DarumaMobile
				//.inicializar("@SOCKET(HOST="+ IP +";PORT=" +PORTA +")");
                //.inicializar("@BLUETOOTH(ADDRESS="+ IP +")");
            .inicializar("@BLUETOOTH(ADDRESS="+IP+";TIMEOUT=30)");
                  //.inicializar("@SOCKET(HOST="+ IP +";PORT=" +PORTA +")");
		objeto.confParametros("@FRAMEWORK(TRATAEXCECAO=TRUE)");
		//TextView versao = (TextView) findViewById(R.id.textView1);
		//versao.setText(String.valueOf(obj.retornaVersao()));
    
			objeto.iniciarComunicacao();
        
            BLOCO = + ((char) 0x1B) + "@"
                    + ((char) 0x0A)
                    + ((char) 0x1B)+"a1" + EMPRESA_DESCRICAO
                    + ((char) 0x0A)
                    + ((char) 0x0A)
                    + ((char) 0x13) + "CNPJ"             
                    + ((char) 0x3A)             
                    + ((char) 0x13) + CNPJ
                    + ((char) 0x20)             
                    + ((char) 0x12) + "IE"
                    + ((char) 0x3A)             
                    + ((char) 0x12) + INSCR_ESTADUAL            
                    + ((char) 0x0A)
                    + ((char) 0x12) + LOGRADOURO
                    + ((char) 0x2C)
                    + ((char) 0x20)             
                    + ((char) 0x12) + NUMERO
                    + ((char) 0x20)             
                    + ((char) 0x2D)             
                    + ((char) 0x12) + BAIRRO
                    + ((char) 0x20)             
                    + ((char) 0x2D)
                    + ((char) 0x20)             
                    + ((char) 0x12) + MUNICIPIO
                    + ((char) 0x20)             
                    + ((char) 0x2D)
                    + ((char) 0x20)             
                    + ((char) 0x12) + UF
                    + ((char) 0x20)             
                    + ((char) 0x2D)
                    + ((char) 0x20)
                    + ((char) 0x12) + "FONE"
                    + ((char) 0x3A)             
                    + ((char) 0x12) + TELEFONE
                    + ((char) 0x0A) + "" + ((char) 0x0A)             
                    + ((char) 0x1B) + "" + ((char) 0x45) + "------------------------------------------------" + ((char) 0x1B) + "" + ((char) 0x46)
                    + ((char) 0x0A) + "" + ((char) 0x0A)
                    + "NUMERO" + ((char) 0x3A) + NUM_CUPOM
                    + ((char) 0x20) + ((char) 0x20) + "DATA" + ((char) 0x3A) + LIVE_DATA
                    + ((char) 0x20)             
                    + LIVE_HORA
                    + ((char) 0x0A) + "" + ((char) 0x0A)                              
                    + ((char) 0x1B) +"a1" + ((char) 0x1B) + ((char) 0x45) + "PPEDIDO" + ((char) 0x1B) + ((char) 0x46)
					+ ((char) 0x0A) + "" + ((char) 0x0A)
                    + "------------------------------------------------"
                    + ((char) 0x0A)             
                    + "Codigo" + ((char) 0x20)
                    + ((char) 0x1B) + "" + ((char) 0x45) + "DDescricao" + ((char) 0x20) + ((char) 0x20) + ((char) 0x20) + ((char) 0x20) + ((char) 0x20) + ((char) 0x20) + ((char) 0x1B) + "" + ((char) 0x46)          
                    + "Qtde UN" + ((char) 0x20) + ((char) 0x20)
                    + ((char) 0x1B) + "" + ((char) 0x45) + "VVl Unit" + ((char) 0x20) + ((char) 0x20) + ((char) 0x1B) + "" + ((char) 0x46)
                    + ((char) 0x1B) + "" + ((char) 0x45) + "VVl Total" + ((char) 0x1B) + "" + ((char) 0x46)          
                    + ((char) 0x0A)
                    + "------------------------------------------------"
                    + ((char) 0x0A)             
                    + ((char) 0x12) + VENDA_ITENS             
                    + ((char) 0x1B) + "" + ((char) 0x45) + "________________________________________________" + ((char) 0x1B) + "" + ((char) 0x46)            
                    + ((char) 0x0A) + "" + ((char) 0x0A)
                    + ((char) 0x1B)+"a0" + "Qtde. total de itens:"
                    + "                        " + QUANTIDADE
                    + ((char) 0x0A)             
                    + "Valor total R$:"            
                    + "                          " + LIVE_TOTAL_ITENS
                    + ((char) 0x0A)            
                    + "Desconto R$:"          
                    + "                             " + LIVE_PAGAMENTO_DESC
                    + ((char) 0x0A)
                    + "Acrescimo R$:"          
                    + "                            " + LIVE_PAGAMENTO_ACRES             
                    + ((char) 0x0A) + "" + ((char) 0x0A)
                    + "FORMA DE PAGAMENTO                 VALOR PAGO R$"
                    + ((char) 0x0A)             
                    + VENDA_PAGAMENTO
                    + ((char) 0x0A)             
                    + ((char) 0x13) + "TROCO R$:" + "                                " + LIVE_PAGAMENTO_TROCO   
                    + ((char) 0x0A) + "" + ((char) 0x0A)             
                    + "CLIENTE: "
                    + ((char) 0x1B)+"a1" + CLIENTE             
                    + ((char) 0x0A) + ((char) 0x0A) + ((char) 0x0A)               
                    + ((char) 0x1B)+"a1" + "NAO CONTEM VALOR FISCAL!" 
                    + ((char) 0x0A) + ((char) 0x0A)            
                    + ((char) 0x1B) +"a1" + MENSAGEM     
                    + ((char) 0x0A) + "" + ((char) 0x0A)
                    + ((char) 0x1B)+"a0" + "OPERADOR:" + "" + USUARIO
                    + ((char) 0x0A)             
                    + COMANDA             
                    + ((char) 0x0A) + "" + ((char) 0x0A)             
                    + ((char) 0x1B)+"a1"+  ((char) 0x1B) + ((char) 0x45) + "wwww.livesistemas.com" + ((char) 0x1B) + ((char) 0x46) + ((char) 0x0A)
                    + ((char) 0x0A) + "" + ((char) 0x0A) + "" + ((char) 0x0A) + "" + ((char) 0x0A) + "" + ((char) 0x0A) + "" + ((char) 0x0A)
                    + ((char) 0x1B) + "" + ((char) 0x6d);
			

			objeto.enviarComando(BLOCO);             
                    //+ ((char) 0x0A));
          //objeto.fecharComunicacao();
            
        }
    
    if(TIPO.equals("V") && TIPO_PDV.equals("Fiscal")){
            		// Pega a hora
Date dat= new Date();
//http://www.technotalkative.com/android-json-parsing/

        char[] chrRetorno = new char[1164];
    	DarumaMobile objeto = DarumaMobile
				//.inicializar("@SOCKET(HOST="+ IP +";PORT=" +PORTA +")");
                //.inicializar("@BLUETOOTH(ADDRESS="+ IP +")");
            .inicializar("@BLUETOOTH(ADDRESS="+IP+";TIMEOUT=50)");
		objeto.confParametros("@FRAMEWORK(TRATAEXCECAO=TRUE)");
		//TextView versao = (TextView) findViewById(R.id.textView1);
		//versao.setText(String.valueOf(obj.retornaVersao()));
    
			objeto.iniciarComunicacao();
			

			objeto.enviarComando(+ ((char) 0x1B) + "@"
                    + ((char) 0x0A)
                    + ((char) 0x1B)+"a1" + EMPRESA_DESCRICAO
                    + ((char) 0x0A) + ((char) 0x0A)             
                    + ((char) 0x13) + "CNPJ"            
                    + ((char) 0x3A)
                    + ((char) 0x13) + CNPJ + ((char) 0x20)
                    + ((char) 0x13) + "IE"            
                    + ((char) 0x3A)
                    + ((char) 0x13) + INSCR_ESTADUAL
                    + ((char) 0x0A)             
					+ ((char) 0x0E) + "" + ((char) 0x14) + ((char) 0x1B) + ((char) 0x45) + LIVE_RAZAO_SOCIAL + ((char) 0x1B) + ((char) 0x46)
                    + ((char) 0x0A)
                    + ((char) 0x12) + LOGRADOURO
                    + ((char) 0x2C)
                    + ((char) 0x20)             
                    + ((char) 0x12) + NUMERO
                    + ((char) 0x20)             
                    + ((char) 0x2D)             
                    + ((char) 0x12) + BAIRRO
                    + ((char) 0x20)             
                    + ((char) 0x2D)
                    + ((char) 0x20)             
                    + ((char) 0x12) + MUNICIPIO
                    + ((char) 0x20)             
                    + ((char) 0x2D)
                    + ((char) 0x20)             
                    + ((char) 0x12) + UF
                    + ((char) 0x20)             
                    + ((char) 0x2D)
                    + ((char) 0x20)
                    + ((char) 0x12) + "FONE"
                    + ((char) 0x3A)             
                    + ((char) 0x12) + TELEFONE
                    + ((char) 0x0A) + "" + ((char) 0x0A)             
                    + "------------------------------------------------" 
                    + ((char) 0x0A)
                    + ((char) 0x1B) + "" + ((char) 0x45) + "Documento Auxiliar da Nota Fiscal de Consumidor Eletronica" + ((char) 0x1B) + "" + ((char) 0x46) + ((char) 0x1B) + "" + ((char) 0x6A) + "1"  + ((char) 0x0A)           
                    + "------------------------------------------------"            
                    + ((char) 0x0A)             
                    + "Codigo" + ((char) 0x20)
                    + ((char) 0x1B) + "" + ((char) 0x45) + "DDescricao" + ((char) 0x20) + ((char) 0x20) + ((char) 0x20) + ((char) 0x20) + ((char) 0x20) + ((char) 0x20) + ((char) 0x1B) + "" + ((char) 0x46)          
                    + "Qtde UN" + ((char) 0x20) + ((char) 0x20)
                    + ((char) 0x1B) + "" + ((char) 0x45) + "VVl Unit" + ((char) 0x20) + ((char) 0x20) + ((char) 0x1B) + "" + ((char) 0x46)
                    + ((char) 0x1B) + "" + ((char) 0x45) + "VVl Total" + ((char) 0x1B) + "" + ((char) 0x46)          
                    + ((char) 0x0A)
                    + ((char) 0x12) + VENDA_ITENS             
                    + ((char) 0x1B) + "" + ((char) 0x45) + "________________________________________________" + ((char) 0x1B) + "" + ((char) 0x46)            
                    + ((char) 0x0A) + "" + ((char) 0x0A)
                    + ((char) 0x1B)+"a0" + "Qtde. total de itens:"
                    + "                       " + QUANTIDADE             
                    + ((char) 0x0A)             
                    + "Valor total R$:"            
                    + "                          " + LIVE_TOTAL_ITENS
                    + ((char) 0x0A)             
                    + ACRES_DESC             
                    + ((char) 0x1B)+"a0" ((char) 0x1B)+"!"+((char) 0x08) + "Valor a Pagar R$                         " + VLR_RECEBER +((char) 0x1B)+"!"+((char) 0x00)  
                    + ((char) 0x0A)
                    + ((char) 0x1B)+"a0" + "FORMA DE PAGAMENTO                 VALOR PAGO R$"
                    + ((char) 0x0A)             
                    + ((char) 0x13) + VENDA_PAGAMENTO
                    + ((char) 0x0A)
                    + ((char) 0x13) + "TROCO R$" + "                                 " + LIVE_PAGAMENTO_TROCO   
                    + ((char) 0x0A) + ((char) 0x0A)             
                    + ((char) 0x1B)+"a1" ((char) 0x1B)+"!"+((char) 0x08) + "Consulte pela Chave de Acesso em" + ((char) 0x1B)+"!"+((char) 0x00)
                    + ((char) 0x0A)             
                    + ((char) 0x1B)+"a1" + URL_CONSULTA
                    + ((char) 0x0A)             
                    + ((char) 0x1B)+"a1" + CHAVE_ACESSO          
                    + ((char) 0x0A) + ((char) 0x0A)
                    + ((char) 0x1B)+"a1" + ((char) 0x1B) + "" + ((char) 0x45) + CONSUMIDOR + "" + ((char) 0x1B) + "" + ((char) 0x46) + CLIENTE
                    + ((char) 0x0A) + ((char) 0x0A)
                    ((char) 0x1B)+"!"+((char) 0x08) + "NFC-e nº " + NUM_CUPOM + ((char) 0x20) + "Serie " + SERIE + ((char) 0x20) + LIVE_DATA + ((char) 0x20) + LIVE_HORA + ((char) 0x1B) + "" + ((char) 0x46) + ((char) 0x1B) + "" + ((char) 0x6A) + "1"  + ((char) 0x0A)
                    + ((char) 0x1B) + "" + ((char) 0x45) + "Protocolo de autorizacao:" + ((char) 0x1B) + "" + ((char) 0x46) +  PROTOCOLO + ((char) 0x1B) + "" + ((char) 0x6A) + "1"  + ((char) 0x0A)
                    + ((char) 0x1B) + "" + ((char) 0x45) + "Data da autorizacao" + ((char) 0x1B) + "" + ((char) 0x46) + ((char) 0x20) + DATA_HORA_AUTORIZACAO + ((char) 0x1B)+"!"+((char) 0x00)
                    + ((char) 0x0A) + ((char) 0x0A)
                    + ((char) 0x1B)+"a1" + "EMITIDA EM AMBIENTE DE HOMOLOGAÇÃO - SEM VALOR   FISCAL"
                    + ((char) 0x0A) + ((char) 0x0A)             
                    + montarQrCode(LIVE_QRCODE, "H", "4")
                    + ((char) 0x0A)             
                    + ((char) 0x1B) +"a1" + MENSAGEM            
                    + ((char) 0x0A)
                    + ((char) 0x1B) +"a1" + "Tributos Totais Incidentes (Lei Federal 12.741/2012) R$: " + LIVE_TOTAL_TRIBUTOS
                    + ((char) 0x0A) + ((char) 0x0A)             
                    + ((char) 0x1B)+"a0" + "OPERADOR:" + "" + USUARIO
                    + ((char) 0x0A)             
                    + COMANDA             
                    + ((char) 0x0A) + "" + ((char) 0x0A)             
                    + ((char) 0x1B)+"a1"+  ((char) 0x1B) + ((char) 0x45) + "wwww.livesistemas.com" + ((char) 0x1B) + ((char) 0x46) + ((char) 0x0A)             
                    + ((char) 0x0A) + "" + ((char) 0x0A) + "" + ((char) 0x0A) + "" + ((char) 0x0A)
                    + ((char) 0x1B) + "" + ((char) 0x6d));             
                    //+ ((char) 0x0A));
          //objeto.fecharComunicacao();
            
        }
        
  if(TIPO.equals("C")){        
            		// Pega a hora
Date dat= new Date();
//http://www.technotalkative.com/android-json-parsing/

        char[] chrRetorno = new char[1164];
    	DarumaMobile objeto = DarumaMobile
				//.inicializar("@SOCKET(HOST="+ IP +";PORT=" +PORTA +")");
                    //.inicializar("@BLUETOOTH(ADDRESS="+ IP +")");
                    .inicializar("@BLUETOOTH(ADDRESS="+IP+";TIMEOUT=30)");
		objeto.confParametros("@FRAMEWORK(TRATAEXCECAO=TRUE)");
		//TextView versao = (TextView) findViewById(R.id.textView1);
		//versao.setText(String.valueOf(obj.retornaVersao()));
    
			objeto.iniciarComunicacao();
			

			objeto.enviarComando(+ ((char) 0x1B) + "@"
                    + ((char) 0x0A)
                    + ((char) 0x1B)+"a1" + EMPRESA_DESCRICAO
                    + ((char) 0x0A) + ((char) 0x0A)
                    + ((char) 0x13) + "CNPJ"             
                    + ((char) 0x3A)             
                    + ((char) 0x13) + CNPJ
                    + ((char) 0x20)             
                    + ((char) 0x12) + "IE"
                    + ((char) 0x3A)             
                    + ((char) 0x12) + INSCR_ESTADUAL            
                    + ((char) 0x0A)
                    + ((char) 0x12) + LOGRADOURO
                    + ((char) 0x2C)
                    + ((char) 0x20)             
                    + ((char) 0x12) + NUMERO
                    + ((char) 0x20)             
                    + ((char) 0x2D)             
                    + ((char) 0x12) + BAIRRO
                    + ((char) 0x20)             
                    + ((char) 0x2D)
                    + ((char) 0x20)             
                    + ((char) 0x12) + MUNICIPIO
                    + ((char) 0x20)             
                    + ((char) 0x2D)
                    + ((char) 0x20)             
                    + ((char) 0x12) + UF
                    + ((char) 0x20)             
                    + ((char) 0x2D)
                    + ((char) 0x20)
                    + ((char) 0x12) + "FONE"
                    + ((char) 0x3A)             
                    + ((char) 0x12) + TELEFONE
                    + ((char) 0x0A) + ((char) 0x0A)             
                    + ((char) 0x1B) + ((char) 0x45) + "------------------------------------------------" + ((char) 0x1B) + "" + ((char) 0x46)
                    + ((char) 0x0A) + ((char) 0x0A)
                    + "MESA"
                    + ((char) 0x3A) + ((char) 0x20) + MESA             
                    + ((char) 0x20) + ((char) 0x20) + "DATA" + ((char) 0x3A) + LIVE_DATA
                    + ((char) 0x20) + LIVE_HORA + ((char) 0x20) + ((char) 0x20)
                    + ((char) 0x0A) + ((char) 0x0A)
                    + ((char) 0x1B) +"a1" + ((char) 0x1B) + ((char) 0x45) + "CCONFERENCIA DE MESA" + ((char) 0x1B) + ((char) 0x46)
                    + ((char) 0x0A)             
                    + COMANDA             
					+ ((char) 0x0A)
                    + "------------------------------------------------"
                    + ((char) 0x0A)             
                    + "Codigo" + ((char) 0x20)
                    + ((char) 0x1B) + "" + ((char) 0x45) + "DDescricao" + ((char) 0x20) + ((char) 0x20) + ((char) 0x20) + ((char) 0x20) + ((char) 0x20) + ((char) 0x20) + ((char) 0x1B) + "" + ((char) 0x46)          
                    + "Qtde UN" + ((char) 0x20) + ((char) 0x20)
                    + ((char) 0x1B) + "" + ((char) 0x45) + "VVl Unit" + ((char) 0x20) + ((char) 0x20) + ((char) 0x1B) + "" + ((char) 0x46)
                    + ((char) 0x1B) + "" + ((char) 0x45) + "VVl Total" + ((char) 0x1B) + "" + ((char) 0x46)          
                    + ((char) 0x0A)
                    + "------------------------------------------------"             
                    + ((char) 0x0A)
                    + ((char) 0x12) + VENDA_ITENS             
                    + ((char) 0x1B) + "" + ((char) 0x45) + "________________________________________________" + ((char) 0x1B) + "" + ((char) 0x46)            
                    + ((char) 0x0A) + "" + ((char) 0x0A)               
                    + ((char) 0x1B)+"a0" + "subtotal - R$:"          
                    + "                           " + TOTAL_ITENS
                    + ((char) 0x0A)
                    + "Total por ocupante - R$:"          
                    + "                 " + OCUPANTE             
                    + ((char) 0x0A) + "" + ((char) 0x0A)             
                    + "CLIENTE: "
                    + ((char) 0x1B)+"a1" + CLIENTE             
                    + ((char) 0x0A) + ((char) 0x0A)
                    + ((char) 0x1B)+"a1" + "NAO CONTEM VALOR FISCAL!" 
                    + ((char) 0x0A) + ((char) 0x0A)             
                    + ((char) 0x1B) +"a1" + MENSAGEM     
                    + ((char) 0x0A) + "" + ((char) 0x0A)
                    + ((char) 0x1B)+"a0" + "OPERADOR:" + "" + USUARIO
                    + ((char) 0x0A) + "" + ((char) 0x0A)             
					+ ((char) 0x1B)+"a1"+  ((char) 0x1B) + ((char) 0x45) + "wwww.livesistemas.com" + ((char) 0x1B) + ((char) 0x46)
                    + ((char) 0x0A) + ((char) 0x0A) + ((char) 0x0A) + ((char) 0x0A) + ((char) 0x0A) + ((char) 0x0A)             
                    + ((char) 0x0A));
          //objeto.fecharComunicacao();
  
  }
        
        
    }
	
		}
}
