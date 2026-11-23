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

    private static final Path dictionaryPath = Path.of("src/words.txt");

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            printMenu();
            String choice = scanner.nextLine().trim();

            if ("0".equals(choice)) {
                System.out.println("Выход из игры. До встречи!");
                break;
            }

            if (!"1".equals(choice)) {
                System.out.println("Неверный выбор. Введите 1 для старта или 0 для выхода.");
                continue;
            }

            String secretWord;
            try {
                secretWord = pickRandomWord(dictionaryPath);
            } catch (IOException e) {
                System.out.println("Не удалось прочитать словарь из файла: " + dictionaryPath);
                break;
            }

            playGame(secretWord, scanner);

            if (!askPlayAgain(scanner)) {
                System.out.println("Спасибо за игру! До встречи!");
                break;
            }
        }

        scanner.close();
    }

    private static void printMenu() {
        System.out.println("\n===============================");
        System.out.println("         ВИСЕЛИЦА");
        System.out.println("===============================");
        System.out.println("1 - Новая игра");
        System.out.println("0 - Выход");
        System.out.print("Выберите пункт меню: ");
    }

    private static boolean askPlayAgain(Scanner scanner) {
        while (true) {
            System.out.print("\nСыграть ещё раз? (1 - да / 0 - нет): ");
            String answer = scanner.nextLine().trim();

            if ("1".equals(answer)) {
                return true;
            }
            if ("0".equals(answer)) {
                return false;
            }
            System.out.println("Введите 1 (да) или 0 (нет).");
        }
    }

    private static void playGame(String secretWord, Scanner scanner) {
        char[] guessedWord = createMaskedWord(secretWord);
        int currentMistakes = 0;

        System.out.println("\n--- Новая партия началась! ---");
        System.out.println("Слово: " + new String(guessedWord));

        while (currentMistakes < maxMistakes) {
            char letter = readValidLetter(scanner);
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
                return;
            }
        }

        System.out.println("\nТы проиграл! У тебя не осталось жизней. Загаданное слово: " + secretWord);
    }

    private static char readValidLetter(Scanner scanner) {
        while (true) {
            System.out.print("\nВведите одну заглавную русскую букву: ");
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                System.out.println("Пустой ввод запрещён. Введите одну заглавную русскую букву.");
                continue;
            }

            if (input.length() != 1) {
                System.out.println("Нужно ввести ровно одну заглавную русскую букву.");
                continue;
            }

            char letter = input.charAt(0);
            if (!isAllowedRussianUpperLetter(letter)) {
                System.out.println("Разрешены только заглавные русские буквы (А–Я).");
                continue;
            }

            return letter;
        }
    }

    private static boolean isAllowedRussianUpperLetter(char letter) {
        return (letter >= 'А' && letter <= 'Я') || letter == 'Ё';
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
