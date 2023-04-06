import java.io.RandomAccessFile;

/**
 * <pre>
 * CEP03.java
 * 
 * Compilador para o Exercício Prático 03 da matéria de Arquitetura de Computadores II.
 * 
 * descrição: lê um programa fonte passado como argumento
 * com os dados e os mnemônicos e gera um arquivo hexa
 * correspondente aos dados e instruções.
 * 
 * Tabela com conjunto de instruções validas:
 * +---------------+---------------+-------------+
 * | Função        | Mnemônico     | Código Hexa |
 * +---------------+---------------+-------------+
 * | F =  ~A       | An            |     0       |
 * | F =  ~B       | Bn            |     1       |
 * | F =   1       | umL           |     2       |
 * | F =   0       | zeroL         |     3       |
 * | F = ~(A . B)  | nAeB          |     4       |
 * | F = ~(A + B)  | nAoB          |     5       |
 * | F =   A ^ B   | AxB           |     6       |
 * | F =   A . ~B  | AeBn          |     7       |
 * | F =  ~A + B   | AnoB          |     8       |
 * | F = ~(A ^ B)  | nAxB          |     9       |
 * | F =   A + B   | AoB           |     A       |
 * | F =   A . B   | AeB           |     B       |
 * | F =  ~A . B   | AneB          |     C       |
 * | F =   A + ~B  | AoBn          |     D       |
 * | F =   B       | copiaB        |     E       |
 * | F =   A       | copiaA        |     F       |
 * +---------------+---------------+-------------+
 * detalhes: - o programa começa a ler após encontrar a palavra inicio
 *           - o programa termina após encontrar a palavra fim
 *           - o programa ignora espacos em brancos em comandos
 *           - a extensão do programa fonte deve ser .ula
 *           - a execução do compilador dever ter pelo menos um argumento e ele deve ser o nome do arquivo (.ula)
 *           - o arquivo de saida será um .hex com o mesmo nome do programa fonte
 *           - o que for digitado antes de "inicio:" e depois de "fim." será desconsiderado
 * 
 * autores: - Leandro Guido Lorenzini Santos
 *          - 
 *          - 
 *          - 
 * data   : 03/04/2023
 * versão : v0.1
 * </pre>
 */
class CEP03 {

    private static long linha = 1; /* controle de quantos '\n' foram feitos, para mostrar aonde está o erro do programa */
    private static final char separador = ' ';
    private static final String [] tabelaDeMnemonicos = {
        "An",
        "Bn",
        "umL",
        "zeroL",
        "nAeB",
        "nAoB",  
        "AxB",   
        "AeBn",  
        "AnoB",  
        "nAxB",  
        "AoB",   
        "AeB",   
        "AneB",  
        "AoBn",  
        "copiaB",
        "copiaA"
    };

    /**
     * getExtensaoDoArquivo () - pega o nome de um arquivo e retorna uma String contendo a extensão dele.
     * @param nomeDoArquivo
     * @return String com a extensão do arquivo no seguinte formato: ".txt"
     * @throws Exception para tratar arquivos sem extensão.
     */
    private static String getExtensaoDoArquivo(String nomeDoArquivo) throws Exception {
        int indexDoPonto = -1;

     // percorre a string com o nome do arquivo
        for (int i = 0; i < nomeDoArquivo.length(); i++) {

         // se encontrar um ponto, salva o index aonde o ponto foi encontrado e sai da repetição
            if(nomeDoArquivo.charAt(i) == '.') {
                indexDoPonto = i;
                i = nomeDoArquivo.length();
            } // end if

        } // end for

     // se não encontrou '.' na String joga uma exceção
        if(indexDoPonto == -1) throw new Exception("Arquivo sem extensão.");

        return nomeDoArquivo.substring(indexDoPonto, nomeDoArquivo.length()); 
    } // end getExtensaoDoArquivo()

    /**
     * getNomeDoArquivoSemExtensaoULA ()
     * @param nomeDoArquivo
     * @return retorna o nome do arquivo sem a extensão .ula
     */
    private static String getNomeDoArquivoSemExtensaoULA(String nomeDoArquivo) {
        final int tamanhoDaExtensao = 4;
        return nomeDoArquivo.substring(0, nomeDoArquivo.length() - tamanhoDaExtensao);
    } // end getNomeDoArquivoSemExtensaoULA()

    /**
     * encontrarInicio () - encontra a palavra "inicio:" no arquivo de programa
     * @param programa RandomAcessFile para ler o programa
     * @throws Exception se não encontrar a palavra "inicio:"
     */
    private static void encontrarInicio (RandomAccessFile programa) throws Exception {

        final String inicio = "inicio:";
        char buffer; /* guardar caractere lido */
        boolean encontrou = false;
        long possivelInicio = -1; /* para salvar posição do ultimo 'i' lido */

     // passar pelo arquivo procurando a primeira letra de "inicio:"
        for (long i = 0; i < programa.length() && !encontrou; i++) {
            buffer = (char) programa.read();
            if(buffer == '\n') linha++;
         // se o caractere for 'i', comeca a ler do arquivo e comparar com a palavra "inicio:"
            if(buffer == 'i') {
                possivelInicio = programa.getFilePointer() - 1; 
             // se a repetição acabar e encontrou == true, é porque a palavra "inicio:" foi encontrada
             // caso encontre um char que não satisfaça as condições, retorna o apontador do arquivo para o ultimo 'i' lido e volta a fazer a repetição superior
                for (int j = 1; j < inicio.length(); j++) {
                    buffer = (char) programa.read();
                    if(buffer == 'i') {
                        possivelInicio = programa.getFilePointer() - 1;
                    } // end if
                    if(buffer == inicio.charAt(j)) {
                        encontrou = true;
                    } else {
                        programa.seek(possivelInicio); /* voltar para a ultima letra 'i' encontrada na leitura */
                        encontrou = false;
                        j = inicio.length();
                    } // end if
                } // end for
            } // end if
        } // end for

     // se chegou no fim do arquivo e não encontrou o inicio -> jogar exceção
        if(encontrou == false) throw new Exception("Erro na linha " + linha + " : não foi possivel encontrar inicio do programa");

    } // end encontrarInicio ()

    /**
     * getCodigoDoMnemonico () - transforma um mneumonico em seu codigo
     * @param mneumonico 
     * @return codigo do mnemonico 
     * @throws Exception se o mneumonico for invalido
     */
    private static int getCodigoDoMnemonico (String mneumonico) throws Exception {
        int codigoDoMnemonico = -1;
        for (int i = 0; i < tabelaDeMnemonicos.length; i++) {
            if(mneumonico.compareTo(tabelaDeMnemonicos[i]) == 0) {
                codigoDoMnemonico = i;
                i = tabelaDeMnemonicos.length;
            } // end if
        } // end for
        if(codigoDoMnemonico == -1) throw new Exception("Erro na linha " + linha + " : mneumônico invalido ou não encontrado");
        return codigoDoMnemonico;
    } // end getCodigoDoMnemonico ()

    /**
     * lerComandoDoPrograma () 
     * @param programa RandomAcessFile para ler o programa
     * @return comando, sem espaços em brancos
     * @throws Exception no caso de comandos invalidos ou incorretos
     */
    private static String lerComandoDoPrograma (RandomAccessFile programa) throws Exception{

        String comando = "";
        char buffer; /* guardar caractere lido */

     // Repetir ate encontrar um charactere de parada ('.' ou ';') ou chegar no fim do arquivo
        do {
         // leitura do programa  para o buffer
            buffer = (char) programa.read();
            if(buffer == '\n') linha++;
         // copiar o buffer para comando somente se não for um caractere em branco
            if(!Character.isWhitespace(buffer)) {
                comando += buffer;
            } // end if
        } while( programa.getFilePointer() != programa.length() && buffer != ';' && buffer != '.' );

     // Tratar erros
        if( buffer == ';' ) {
            if( comando.charAt(0) != 'X' && comando.charAt(0) != 'Y' && comando.charAt(0) != 'W' ) throw new Exception("Erro na linha " + linha + " : variavel inexistente ou incorreta");
            if( comando.charAt(1) != '=') throw new Exception("Erro na linha " + linha + " : faltou sinal de atribuição (=)");
        } else {
            if( comando.compareTo("fim." ) != 0) throw new Exception("Erro na linha " + linha + " : não encontrou o comando de parada corretamente.");
        } // end if

        return comando;
    } // end lerComandoDoPrograma ()

    /**
     * getValor () - pega o valor entre '=' e o ';'
     * @param comando
     * @return valor retirado do comando
     * @throws Exception se os valores da variavel não couber em 1 digito hexadecimal
     */
    private static int getValor (String comando) throws Exception {
        int val = Integer.parseInt(comando.substring(2, comando.length() - 1));
        if(0 <= val && val < 16) {
            return val;
        } else {
            throw new Exception("Erro na linha " + linha + " : valor fora dos limites da base hexadecimal");
        } // end if
    } // end getValor ()

    /**
     * getCodigoHex () - transforma X, Y e o mneumonico em hexadecimal
     * @param X
     * @param Y
     * @param comando
     * @return string hexadecimal
     * @throws Exception se o mneumonico for invalido
     */
    private static String getCodigoHex(int X, int Y, String comando) throws Exception {
        return Integer.toHexString(X) + Integer.toHexString(Y) + Integer.toHexString(getCodigoDoMnemonico (comando.substring(2, comando.length() - 1)));
    } // end getCodigoHex ()

    public static void main(String[] args) throws Exception {
     // Variaveis do programa
        String comando = "";
        int X = -1, Y = -1; // entradas

     // OBS: args[0] deve conter o nome do arquivo de programa
     // Tratamento de exceções
        if (args.length == 0) throw new Exception("Poucos parametros.");
        if (!getExtensaoDoArquivo(args[0]).equalsIgnoreCase(".ula")) throw new Exception("Extensão do arquivo invalida.");

     // Abrindo arquivo de entrada e saida
        RandomAccessFile programa = new RandomAccessFile(args[0], "r");
        RandomAccessFile saida = new RandomAccessFile(getNomeDoArquivoSemExtensaoULA(args[0])+".hex", "rw");
        saida.setLength(0); // limpar arquivo de saida

     // Tentar encontrar inicio do programa
        encontrarInicio(programa);

     // ps: nesse momento o apontador do arquivo do programa aponta para o inicio do programa
        do {
         // fazendo leituras de comandos
         // comando => string, sem espaços em branco finalizda por '.' ou ';'
            comando = lerComandoDoPrograma(programa);
            switch(comando.charAt(0)) {
             // caso o comando seja atribuição de um valor para X
                case 'X':   X = getValor(comando); break;
             // caso o comando seja atribuição de um valor para Y 
                case 'Y':   Y = getValor(comando); break;
             // caso o comando seja um mneumonico atribuido a saida
                case 'W':   if(X == -1 || Y == -1) throw new Exception ("Erro na linha " + linha + " : variaveis X e Y ainda nao foram definidas");
                            saida.writeChars(getCodigoHex(X, Y, comando) + separador);
                            break;
            } // end switch
     // parar caso encontre o comando de parada ("fim.")
        } while (comando.compareTo("fim.") != 0);

     // Fechamento dos arquivos
        saida.close();
        programa.close();

    } // end main
} // end class CEP03