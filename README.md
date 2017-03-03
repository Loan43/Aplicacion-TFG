# Aplicacion Análisis de Fondos de Inversión (AAFI-TFG)

Como TFG se realiza una aplicación de escritorio con la finalidad de hacer estudios cualitativos y cuantitativos del rendimiento de activos financieros. En particular, presentará gráficas e indicadores que asistan en la la toma de decisiones a los inversores de fondos de inversión.

## Estado

El proyecto se encuentra en el proceso de desarrollo de la capa modelo.

### Requisitos

1. Instalar MySQL.
2. Crear una base de datos dentro de MySQL con nombre aacfi usuario "test" password "test" (o modificar el cfg de hibernate src/main/resources/hibernate.cfg.xml con los datos deseados).
3. Instalación de un IDE. Para el desarrollo se ha utilizado Eclipse Neon 2.
4. Descargar e instalar el gestor de proyectos Maven (Eclipse Neon ofrece una opción para su instalación).


### Ejecución

Para ejecutar los tests de la capa modelo debemos descargar el proyecto de este repositorio e importarlo desde Ecplipse mediante File -> Import -> Maven -> Existing Maven Proyects.

Una vez importado esde ecplipse basta con desplazarse a la carpeta principal del proyecto (Aplicacion-TFG) click derecho, Run As -> Maven install.  

Como alternativa nos situamos en la carpeta en la que hemos descargado el proyecto y ejecutamos:

```
mvn install
```

Con cualquiera de las dos opciones Maven instalará las librerias necesarias y correrá los tests de la capa modelo.

