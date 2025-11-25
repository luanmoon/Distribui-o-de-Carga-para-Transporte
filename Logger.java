import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Logger {
    
    private static final String NOME_ARQUIVO = "log_avl.txt";

    public static void clearLog() {
        try (FileWriter fw = new FileWriter(NOME_ARQUIVO, false)) {
        } catch (IOException e) {
            System.err.println("Não foi possível limpar o arquivo de log.");
            e.printStackTrace();
        }
    }

    public static void log(String mensagem) {
        System.out.println(mensagem);

        try (FileWriter fw = new FileWriter(NOME_ARQUIVO, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            
            out.println(mensagem);

        } catch (IOException e) {
            System.err.println("Não foi possível escrever no arquivo de log.");
            e.printStackTrace();
        }
    }
}