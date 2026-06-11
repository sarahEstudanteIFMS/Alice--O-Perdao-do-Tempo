package com.alice;

/**
 * Launcher separado de MainApp.
 *
 * Quando uma classe que estende javafx.application.Application é usada
 * diretamente como Main-Class (ex: via 'mvn exec:java' ou o botao Run do
 * NetBeans), a JVM verifica se o modulo JavaFX esta no module-path e falha
 * com "JavaFX runtime components are missing", mesmo que os jars estejam
 * no classpath.
 *
 * Usando esta classe (que NAO estende Application) como ponto de entrada,
 * essa verificacao e evitada e o JavaFX roda normalmente via classpath.
 */
public class Launcher {
    public static void main(String[] args) {
        MainApp.main(args);
    }
}
