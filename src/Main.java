import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Scanner;

public class Main {
	public static void main(String[] args) throws IOException {
		Scanner input = new Scanner(System.in);
		BufferedImage decrypted = null;

		System.out.println("Select a method of en/decryption:"); // All methods are reversible, though only 3 and 4 will produce images
		System.out.println("1. Bitwise NOT");
		System.out.println("2. Reverse");
		System.out.println("3. XOR with key");
		System.out.println("4. Invert colors");
		System.out.print("Enter an option: ");

		switch (input.next().charAt(0)) {
			case '1': {
				System.out.print("Enter the name of the encrypted file: ");
				byte[] encrypted = Files.readAllBytes(new File(input.next()).toPath());
				decrypted = not(encrypted);
				break;
			}
			case '2': {
				System.out.print("Enter the name of the encrypted file: ");
				byte[] encrypted = Files.readAllBytes(new File(input.next()).toPath());
				decrypted = reverse(encrypted);
				break;
			} case '3': {
				System.out.print("Enter the name of the encrypted file: ");
				BufferedImage encrypted = ImageIO.read(new File(input.next())); // JPG can fail
				System.out.print("Enter the name of the key file: ");
				BufferedImage key = ImageIO.read(new File(input.next()));
				assert encrypted.getHeight() == key.getHeight() && encrypted.getWidth() == key.getWidth();
				decrypted = xor(encrypted, key);
				break;
			}
			case '4': {
				System.out.print("Enter the name of the encrypted file: ");
				BufferedImage encrypted = ImageIO.read(new File(input.next())); // JPG to transparent PNG can be weird
				decrypted = invert(encrypted);
				break;
			} default:
				System.exit(0);
				break;
		}

		System.out.print("Enter the name of the output file to write to: ");
		String fileName = input.next();
		ImageIO.write(decrypted, fileName.substring(fileName.lastIndexOf('.') + 1), new File(fileName));
	}

	static BufferedImage not(byte[] encrypted) throws IOException {
		for (int i = 0; i < encrypted.length; i++) {
			encrypted[i] = (byte) (encrypted[i] ^ 0xff);
		}
		return ImageIO.read(new ByteArrayInputStream(encrypted));
	}
	
	static BufferedImage reverse(byte[] encrypted) throws IOException {
		for (int i = 0; i < encrypted.length/2; i++) {
			byte temp = encrypted[i];
			encrypted[i] = encrypted[encrypted.length - i - 1];
			encrypted[encrypted.length - i - 1] = temp;
		}
		return ImageIO.read(new ByteArrayInputStream(encrypted));
	}

	static BufferedImage xor(BufferedImage encrypted, BufferedImage key) {
		BufferedImage decrypted = new BufferedImage(key.getWidth(), key.getHeight(), key.getType());

		for (int i = 0; i < key.getHeight(); i++) {
			for (int j = 0; j < key.getWidth(); j++) {
				decrypted.setRGB(j, i, encrypted.getRGB(j, i) ^ key.getRGB(j, i));
			}
		}

		return decrypted;
	}

	static BufferedImage invert(BufferedImage encrypted) {
		BufferedImage decrypted = new BufferedImage(encrypted.getWidth(), encrypted.getHeight(), encrypted.getType());

		for (int i = 0; i < encrypted.getHeight(); i++) {
			for (int j = 0; j < encrypted.getWidth(); j++) {
				decrypted.setRGB(j, i, ~encrypted.getRGB(j, i));
			}
		}

		return decrypted;
	}
}
