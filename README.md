# Lista Smart

Aplicativo Android desenvolvido em Java com foco em listas de compras, cadastro de preços e dashboards para usuários comuns, administradores e mercados.

## Estrutura do projeto

- `ListaSmart/`: projeto Android principal
- `ListaSmart/app/src/main/assets/Database.db`: banco SQLite inicial usado pelo app

## Requisitos

- Android Studio atualizado
- JDK 11
- Android SDK instalado
- Emulador Android ou dispositivo físico com Android 7.0+ (`minSdk 24`)

## Como rodar

1. Abra o Android Studio.
2. Selecione `Open` e escolha a pasta `ListaSmart/`.
3. Aguarde a sincronização do Gradle terminar.
4. Caso o Android Studio peça para instalar SDKs ou plugins, aceite.
5. Escolha um emulador ou conecte um dispositivo físico com depuração USB ativada.
6. Clique em `Run` para instalar e abrir o app.

## Banco de dados

O projeto já inclui um banco SQLite inicial em:

- `ListaSmart/app/src/main/assets/Database.db`

Esse banco é copiado automaticamente para o armazenamento interno do app na primeira execução.

### Importante

Se você alterar manualmente o arquivo `Database.db` dentro de `assets`, a mudança não substitui automaticamente o banco já instalado no aparelho/emulador.

Para forçar o app a usar a nova versão do banco:

1. desinstale o app do dispositivo/emulador;
2. rode o projeto novamente.

## Perfis existentes no app

O sistema trabalha com três tipos principais de usuário:

- `COMUM`: usuário consumidor
- `MERCADO`: usuário supermercado
- `ADMIN`: administrador

Os dados de login e demais registros já dependem do conteúdo do banco SQLite incluído no projeto.

## Logins para teste

### Usuário comum

- `joao@email.com`

### Supermercados

- `russel@listasmart.com`
- `bistek@listasmart.com`
- `giassi@listasmart.com`
- `angeloni@@listasmart.com`
- `combo@listasmart.com`

### Administrador

- `admin@listasmart.com`

### Senha

- `senha123`

Observação: a senha acima é a mesma para todos os logins listados.

## Observações úteis

- O projeto usa Gradle Groovy, não Kotlin DSL.
- O `local.properties` depende do caminho do SDK local de cada máquina.
- Caso o Android Studio mostre erro de ambiente, confira se o JDK selecionado está em Java 11.

## Compatibilidade de versões

Dependendo da versão do Android Studio usada na máquina, pode ser necessário ajustar algumas versões do projeto para conseguir sincronizar e rodar sem erros.

Hoje o projeto está configurado com:

- Android Gradle Plugin em `ListaSmart/gradle/libs.versions.toml`: `9.1.1`
- Gradle Wrapper em `ListaSmart/gradle/wrapper/gradle-wrapper.properties`: `9.3.1`
- toolchain gerada em `ListaSmart/gradle/gradle-daemon-jvm.properties`: Java `21`

### Quando isso pode ser necessário

Se o Android Studio estiver mais antigo, podem aparecer erros de:

- incompatibilidade de Gradle;
- incompatibilidade do Android Gradle Plugin;
- versão de Java/JDK não suportada;
- falha ao sincronizar o projeto.

### O que fazer nesses casos

1. Tente primeiro abrir e sincronizar o projeto normalmente.
2. Se houver erro de versão, confira no Android Studio qual JDK está selecionado.
3. Se necessário, ajuste as versões de:
   - `ListaSmart/gradle/libs.versions.toml`
   - `ListaSmart/gradle/wrapper/gradle-wrapper.properties`
4. Se a IDE reclamar da toolchain, também pode ser necessário revisar `ListaSmart/gradle/gradle-daemon-jvm.properties`.

### Observação

Em outras palavras: o projeto já está pronto para rodar, mas em algumas máquinas pode ser necessário alinhar versões de Gradle, plugin Android e JDK com a versão do Android Studio disponível.

## Tecnologias

- Java
- Android SDK
- SQLite
- Material Components
- Gradle
