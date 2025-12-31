import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class Tester {

    private static final String INPUT_DIR = "testcases/inputs";
    private static final String EXPECTED_OUTPUT_DIR = "testcases/outputs";
    private static final String MY_OUTPUT_DIR = "testcases/my_outputs";

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_YELLOW = "\u001B[33m";

    public static void main(String[] args) {
        File inputFolder = new File(INPUT_DIR);
        File expectedOutputFolder = new File(EXPECTED_OUTPUT_DIR);

        new File(MY_OUTPUT_DIR).mkdirs();

        if (!inputFolder.exists() || !expectedOutputFolder.exists()) {
            System.out.println(ANSI_RED + "HATA: 'testcases/inputs' veya 'testcases/outputs' klasörü bulunamadı!" + ANSI_RESET);
            System.out.println("Lütfen proje ana dizininde bu klasörlerin olduğundan emin ol.");
            return;
        }

        File[] inputFiles = inputFolder.listFiles((dir, name) -> name.endsWith(".txt"));

        if (inputFiles == null || inputFiles.length == 0) {
            System.out.println(ANSI_RED + "Test edilecek input dosyası bulunamadı." + ANSI_RESET);
            return;
        }

        Arrays.sort(inputFiles);

        System.out.println("=== TEST BAŞLIYOR ===");
        int passed = 0;
        int failed = 0;

        for (File inputFile : inputFiles) {
            String inputName = inputFile.getName();
            String expectedOutputName = inputName.replace(".txt", ".txt_out.txt");

            File expectedFile = new File(expectedOutputFolder, expectedOutputName);

            if (!expectedFile.exists()) {
                System.out.println(ANSI_YELLOW + "UYARI: " + inputName + " için beklenen çıktı (" + expectedOutputName + ") bulunamadı. Atlanıyor." + ANSI_RESET);
                continue;
            }

            String myOutputPath = MY_OUTPUT_DIR + File.separator + "my_" + expectedOutputName;

            System.out.print("Test ediliyor: " + String.format("%-25s", inputName));

            Main.network = new HashMap<>();

            long startTime = System.currentTimeMillis();
            try {
                Main.main(new String[]{inputFile.getPath(), myOutputPath});
            } catch (Exception e) {
                System.out.println(ANSI_RED + "[CRASH]" + ANSI_RESET);
                e.printStackTrace();
                failed++;
                continue;
            }
            long endTime = System.currentTimeMillis();
            double duration = (endTime - startTime) / 1000.0;

            boolean isMatch = compareFiles(expectedFile.getPath(), myOutputPath);

            if (isMatch) {
                System.out.println(ANSI_GREEN + "[BAŞARILI] " + ANSI_RESET + "(" + String.format("%.2f", duration) + "s)");
                passed++;
            } else {
                System.out.println(ANSI_RED + "[BAŞARISIZ]" + ANSI_RESET + " (" + String.format("%.2f", duration) + "s)");
                System.out.println("   -> Beklenen: " + expectedFile.getPath());
                System.out.println("   -> Senin Çıktın: " + myOutputPath);
                failed++;
            }
        }

        System.out.println("=====================");
        System.out.println("Toplam: " + (passed + failed));
        System.out.println(ANSI_GREEN + "Geçen:  " + passed + ANSI_RESET);
        System.out.println(ANSI_RED + "Kalan:  " + failed + ANSI_RESET);
    }

    private static boolean compareFiles(String expectedPath, String actualPath) {
        try {
            List<String> expectedLines = Files.readAllLines(Paths.get(expectedPath));
            List<String> actualLines = Files.readAllLines(Paths.get(actualPath));

            expectedLines.removeIf(String::isEmpty);
            actualLines.removeIf(String::isEmpty);

            if (expectedLines.size() != actualLines.size()) {
                return false;
            }

            for (int i = 0; i < expectedLines.size(); i++) {
                String expected = expectedLines.get(i).trim();
                String actual = actualLines.get(i).trim();

                if (!expected.equals(actual)) {
                    return false;
                }
            }
            return true;
        } catch (IOException e) {
            System.out.println("Dosya okuma hatası: " + e.getMessage());
            return false;
        }
    }
}