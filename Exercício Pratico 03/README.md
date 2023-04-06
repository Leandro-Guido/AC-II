# Exercício Pratico 03

## **Arquitetura Proposta**:
Neste exercício você deverá criar 2 programas. Um no hardware externo (Arduino) e outro no PC, que será a 
interface com o usuário. A ideia é ler um programa escrito pelo usuário, transformá-lo em mnemônicos gerando 
outro programa e finalmente passá-lo ao Hardware externo. O resultado será observado nos 4 Leds conectados no 
Hardware externo.

## **Software no PC**

O software no PC foi escrito em Java.

O programa transforma um texto lido de um arquivo nas instruções a serem executadas no Arduino.

Para isso, o programa inicialmente lê um arquivo (.ula) contendo um texto original com os mnemônicos (instruções a serem executadas)
e gera um segundo texto, que é um arquivo (.hex) gravado com os respectivos valores a serem enviados para a porta USB/serial
(ou digitados) porém no formato hexadecimal.

**Tabela com o conjunto de instruções para a ULA:**

| **Função**    | **Mnemônico** |**Código Hexa**|
|---------------|---------------|---------------|
| F =  ~A       | An            |      0        |
| F =  ~B       | Bn            |      1        |
| F =   1       | umL           |      2        |
| F =   0       | zeroL         |      3        |
| F = ~(A . B)  | nAeB          |      4        |
| F = ~(A + B)  | nAoB          |      5        |
| F =   A ^ B   | AxB           |      6        |
| F =   A . ~B  | AeBn          |      7        |
| F =  ~A + B   | AnoB          |      8        |
| F = ~(A ^ B)  | nAxB          |      9        |
| F =   A + B   | AoB           |      A        |
| F =   A . B   | AeB           |      B        |
| F =  ~A . B   | AneB          |      C        |
| F =   A + ~B  | AoBn          |      D        |
| F =   B       | copiaB        |      E        |
| F =   A       | copiaA        |      F        |

### **Exemplo de programa (.ula)**

O programa deve conter a palavra "inicio:" para comecar a execução a partir dela, e acaba ao encontrar o comando "fim.".

OBS: espaços em brancos ('\n', ' ', etc) são ignorados nos comandos.

```
inicio:
    X=4;
    Y=5;
    W=An;
    X=12;
    Y=11;
    W=Bn;
fim.
```

### **Exemplo de saida (.hex)**

Cada instrução é constituida de 3 valores em hexadecimal, o primeiro representa o valor de X, o segundo representa o valor de Y e o ultimo,
o codigo para a instrução desejada.

```
450 cb1
```

## **O Programa no Arduino**
O programa no Arduino que utiliza a entrada serial para receber as entradas necessárias 
ao funcionamento da ULA (arquivo .hex) e as saídas são 4 Leds ligados aos pinos 13, 12, 11 e 10 (o 
bit mais significativo no pino 13 e o menos significativo no pino 10).

No Arduino existe um vetor de tamanho 100 que funciona como a memória (e também os registradores) da Unidade.
Este vetor contém nos quatro primeiros campos (que serão os registradores da máquina) os seguintes 
valores:

- 1a posição = o índice do vetor onde a instrução está armazenada, que chamaremos de PC,
- 2a posição = o conteúdo da variável W ( que contém os resultados das operações),
- 3a posição = o conteúdo da variável X,
- 4a posição = o conteúdo da variável Y.

**O codigo consiste em 2 partes + inicialização:**

### **Inicialização**

1. Cria o vetor de short: mem[100]
2. Valor contido na primeira posição do vetor (PC) é definido como 4
3. As outras posições do vetor são definidas como -1 (significando vazio)

### **1a parte: Carga do programa**

Recebe as instruções do programa e preenche o vetor de memória, a partir da posição 4, com as instruções.

OBS:
- As instruções devem estar separadas por ' ' ou colocadas uma por uma.
- Ao receber a instrução "0" a carga para e a 2a parte se inicia.

### **2a parte: Executar as instruções**

1. Recolhe a instrução apontada pelo PC, se a instrução for -1, não faz nada, caso contrario:

2. Salva o codigo dessa instrução para saber quais operações executar,
guarda os valores de X e Y nos respectivos locais de memória (mem[2] e mem[3]),
executa as operações e salva o resultado em W (mem[1])

3. Após a execução é feito um DUMP, aonde todo o vetor de memória é mostrado.

4. PC é incrementado para preparar para a execução da próxima instrução

