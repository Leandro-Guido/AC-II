
#include <string.h>

#define DELAY 1000

// ----------------------- Constantes globais

const short F3 = 13;
const short F2 = 12;
const short F1 = 11;
const short F0 = 10;

// ----------------------- Variaveis globais

short mem [100];
short *PC = &mem[0];
short *W  = &mem[1];
short *X  = &mem[2];
short *Y  = &mem[3];

// ----------------------- Funcoes

/**
 * valida() - faz a validacao de uma instrucao
 * @param instrucao - String com instrucao
 * @return true se tamanho = 3 e os digitos sao hexadecimais
 *         false caso contrario
 */
bool valida(String instrucao) {
	return instrucao.length() == 3 && isHexadecimalDigit(instrucao[0]) && isHexadecimalDigit(instrucao[1]) && isHexadecimalDigit(instrucao[2]);
} // end valida()

/**
 * getDigitoDaInstrucao () - recolhe um digito da instrucao
 * considerando que ela esta na base 16 e atualiza a instrucao
 * para pegar o proximo digito quando essa funcao for executada
 * @param instrucao - short instrucao na base 16
 * @return digito
 */
short getDigitoDaInstrucao(short *instrucao) {
  short digito = *instrucao % 16;
  *instrucao/=16;
  return digito;
} // end getDigitoDaInstrucao ()

/**
 * mostrarNasLeds() - escreve nas leds, utilizando mascaras
 * de bits, o contido em W
 */
int mostrarNasLeds () {
  digitalWrite(F0,*W & 1);
  digitalWrite(F1,*W & 2);
  digitalWrite(F2,*W & 4);
  digitalWrite(F3,*W & 8);
} // end mostrarNasLeds ()

/**
 * inicializarMemoria() - inicializa o vetor memoria
 * com -1 em todas as posicoes.
 */
void inicilizarMemoria () {
  *PC = 4;
  for(int i = 1; i < 100; i++)
    mem[i] = -1;
} // end inicilizarMemoria ()

/**
 * dump() - mostra o vetor memoria em hexadecimal
 */
void dump () {
  Serial.print((String)"|PC "+ mem[0]+"\t");
  Serial.print("\W ");
  Serial.print(*W&15,HEX);
  Serial.print("|X ");
  Serial.print(*X&15,HEX);
  Serial.print("|Y ");
  Serial.print(*Y&15,HEX);
  Serial.print('|');
  for(int i = 4; i < 100; i++) {
    if(mem[i] != -1) {
      Serial.print(mem[i], HEX);
      Serial.print('|');
    } // end if
  } // end for
  Serial.println();
} // end dump ()

// ----------------------- setup ()

void setup() {
  
  Serial.begin(9600);
  
  pinMode(F3,OUTPUT);
  pinMode(F2,OUTPUT);
  pinMode(F1,OUTPUT);
  pinMode(F0,OUTPUT);
  
  Serial.println("\nCarga do vetor");
  Serial.println("\nDigite \"0\" para finalizar a carga");
  String instrucao = "";
  
  inicilizarMemoria();
  
  int CC = 4;// controle da carga
  do{
    instrucao = Serial.readStringUntil(' ');
    if(valida(instrucao)){
   	  if(CC < 100) {
        mem[CC] = (short)strtol(instrucao.c_str(), nullptr, 16);
        CC++;
     } // end if
   } // end if
  } while (!instrucao.equals("0"));
  Serial.println("Fim da carga.");
  Serial.println("\nExecutando instrucoes.");
  Serial.println("DUMP:");
} // end setup ()

// ----------------------- loop ()

void loop() {
  // OSB: -1 é o indicador de memoria vazia
  if(mem[*PC] != -1) {
   
    int operacao;
    short instrucao = mem[*PC]; /* recupera a instrucao da memoria */
    
 // transforma a instrucao nos valores respectivos
    operacao = getDigitoDaInstrucao(&instrucao);
    *Y = getDigitoDaInstrucao(&instrucao);
    *X = getDigitoDaInstrucao(&instrucao);
    
 // executa a operacao instruida
    switch ( operacao ) {
        case 0 : *W = portanot(*X); break;
        case 1 : *W = portanot(*Y); break;
        case 2 : *W = 0x1; break;
        case 3 : *W = 0x0; break;
        case 4 : *W = portanot(portaand(*X,*Y)); break;
        case 5 : *W = portanot(portaor (*X,*Y)); break;
        case 6 : *W = portaxor(*X,*Y); break;
        case 7 : *W = portaand(*X,portanot(*Y)); break;
        case 8 : *W = portaand(portanot(*X), *Y); break;
        case 9 : *W = portanot(portaxor(*X,*Y)); break;
        case 10: *W = portaor (*X,*Y); break;
        case 11: *W = portaand(*X,*Y); break;
        case 12: *W = portaand(portanot(*X),*Y); break;
        case 13: *W = portaor(*X,portanot(*Y)); break;
        case 14: *W = *Y; break;
        case 15: *W = *X; break;
    } // end switch
    
    dump(); // mostrar vetor de memoria
    mostrarNasLeds();
    delay(DELAY);
    *PC = *PC + 1;
    if(mem[*PC] == -1) {
    	Serial.println("Fim das execucoes");
    } // end if
  } // end if
} // end loop ()

// ----------------------- Portas Lógicas

int portaxor(int a, int b){ return(a^b); }

int portaor(int a, int b){ return(a|b); }

int portaand(int a, int b){ return(a&b); }

int portanot(int a){ return(~a); }