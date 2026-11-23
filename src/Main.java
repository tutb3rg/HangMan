import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Main {
    private static final Random random = new Random();
    private static final int maxMistakes = 6;

    public static void main(String[] args) {
        Path dictionaryPath = Path.of("src/words.txt");
        String secretWord;

        try {
            secretWord = pickRandomWord(dictionaryPath);
        } catch (IOException e) {
            System.out.println("Не удалось прочитать словарь из файла: " + dictionaryPath);
            return;
        }
        playGame(secretWord);
    }

    private static void playGame(String secretWord) {
        char[] guessedWord = createMaskedWord(secretWord);
        int currentMistakes = 0;
        System.out.println("Добро пожаловать в гэмбл");
        System.out.println("Слово: " + new String(guessedWord));

        Scanner scanner = new Scanner(System.in);

        while (currentMistakes < maxMistakes) {
            System.out.println("\nВведите букву: ");
            String input = scanner.nextLine().trim().toUpperCase();
            if (input.isEmpty()) {
                continue;
            }
            char letter = input.charAt(0);
            boolean isHit = revealLetter(secretWord, guessedWord, letter);

            if (!isHit) {
                currentMistakes++;
                System.out.println("Ты не попал! Ошибок: " + currentMistakes + " из " + maxMistakes);
            } else {
                System.out.println("Попал! Такая буква есть!");
            }

            System.out.println("Слово: " + new String(guessedWord));

            if (isWordGuessed(guessedWord)) {
                System.out.println("\nПоздравляю, ты выиграл!");
                System.out.println("Загаданное слово: " + secretWord);
                scanner.close();
                return;
            }
        }
        System.out.println("\nТы проиграл! У тебя не осталось жизней. Загаданное слово: " + secretWord);
        scanner.close();
    }

    private static char[] createMaskedWord(String secretWord) {
        char[] masked = new char[secretWord.length()];
        Arrays.fill(masked, '*');
        return masked;
    }

    private static boolean revealLetter(String secretWord, char[] guessedWord, char letter) {
        boolean isHit = false;
        for (int i = 0; i < secretWord.length(); i++) {
            if (secretWord.charAt(i) == letter) {
                guessedWord[i] = letter;
                isHit = true;
            }
        }
        return isHit;
    }

    private static boolean isWordGuessed(char[] guessedWord) {
        for (char c : guessedWord) {
            if (c == '*') {
                return false;
            }
        }
        return true;
    }

    private static String pickRandomWord(Path path) throws IOException {
        List<String> words = Files.readAllLines(path, StandardCharsets.UTF_8).stream()
                .map(String::trim)
                .filter(word -> !word.isEmpty())
                .toList();

        if (words.isEmpty()) {
            throw new IOException("Файл словаря пуст: " + path);
        }

        int randomIndex = random.nextInt(words.size());
        return words.get(randomIndex).toUpperCase();
    }
}

