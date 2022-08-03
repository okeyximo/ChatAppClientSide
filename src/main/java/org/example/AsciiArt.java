package org.example;

public class AsciiArt {
    public static String art = """
              ___  _             __  __
             / _ \\| | _____ _   _\\ \\/ /
            | | | | |/ / _ \\ | | |\\  /\s
            | |_| |   <  __/ |_| |/  \\\s
             \\___/|_|\\_\\___|\\__, /_/\\_\\
                            |___/     \s
            """;

    public static void displayArt() throws InterruptedException {
        for (int i = 0; i < art.length() ; i++){
            System.out.print(art.charAt(i));
            Thread.sleep(5);
        }
    }
}



