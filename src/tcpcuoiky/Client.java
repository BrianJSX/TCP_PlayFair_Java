/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tcpcuoiky;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
/**
 *
 * @author MinhIT
 */
public class Client {

    private static char[][] matrix;

    public static String encrypt(String data, char[][] matrix) {
        String result = "";
        String[] pairs = addX(data);
        
        for (String pair : pairs) {
            char left = pair.charAt(0);
            int[] left_pos = getPosition(left, matrix);
            char right = pair.charAt(1);
            int[] right_pos = getPosition(right, matrix);

            int left_x, left_y, right_x, right_y;

            if (left_pos[0] == right_pos[0]) {
                left_x = left_pos[0];
                left_y = (left_pos[1] + 1) % 5;
                right_x = right_pos[0];
                right_y = (right_pos[1] + 1) % 5;
            } else if (left_pos[1] == right_pos[1]) {
                left_x = (left_pos[0] + 1) % 5;
                left_y = left_pos[1];
                right_x = (right_pos[0] + 1) % 5;
                right_y = right_pos[1];
            } else {
                left_x = left_pos[0];
                left_y = right_pos[1];
                right_x = right_pos[0];
                right_y = left_pos[1];
            }
            result += matrix[left_x][left_y];
            result += matrix[right_x][right_y];
        }
        return result;
    }

    public static int[] getPosition(char c, char[][] matrix) {
        if (c == 'j') {
            c = 'i';
        }
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                if (c == matrix[i][j]) {
                    return new int[]{i, j};
                }
            }
        }
        return null;
    }

    public static String[] addX(String message) {
        ArrayList<String> result = new ArrayList<String>();
        int i = 0;
        while (i < message.length()) {
            char c1 = message.charAt(i); //charAt bắt đầu vị trí thứ 0
            char c2;
            
            if (i + 1 < message.length()) {
                c2 = message.charAt(i + 1);
            } else {
                c2 = 'x';
            }

            if (c1 == c2) {
                result.add(new String("" + c1 + 'x'));
                i++;
            } else {
                result.add(new String("" + c1 + c2));
                i += 2;
            }
        }

        return (String[]) result.toArray(new String[0]);
    }

    public static char[][] getMatrix(String key) {
        char[][] matrix = new char[5][5];
        int length = 0;

        for (int i = 0; i < key.length(); i++) {
            char curChar = key.charAt(i);
            int firstOccurance = key.indexOf(curChar);
            if (firstOccurance == i) {
                matrix[length / 5][length % 5] = curChar;
                length++;
            }
        }

        char[] alphabet = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
        for (int i = 0; i < alphabet.length; i++) {
            if (key.indexOf(alphabet[i]) == -1) {
                matrix[length / 5][length % 5] = alphabet[i];
                length++;
            }
        }
        return matrix;
    }

    public static void printMatrix(char[][] matrix) {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                System.out.print(matrix[i][j] + "  ");
            }
            System.out.println();
        }
    }

    public static String replaceSpace(String chuoi) {
        String text = "";
        text = chuoi.replaceAll(" ", "");
        return text;
    }

    public static void main(String[] args) throws IOException {
        Socket client = new Socket("localhost", 8888);

        DataInputStream din = new DataInputStream(client.getInputStream());
        DataOutputStream dout = new DataOutputStream(client.getOutputStream());
        Scanner sc = new Scanner(System.in);
        String key = "";
        String text = "";

        boolean check = false;
        while (check == false) {
            try {
                System.out.println("Nhập key:");
                key = sc.nextLine();
                System.out.println("Nhập chuỗi cần mã hóa:");
                text = sc.nextLine();
                check = true;
            } catch (Exception e) {
                sc.nextLine();
            }
        }
        
        matrix = getMatrix(key);
        printMatrix(matrix);
        String cipher = encrypt(replaceSpace(text), matrix);
        System.out.println("Encrypt: " + cipher);
        
        //gửi về cho server
        dout.writeUTF(key);
        dout.writeUTF(cipher);
        
        //nhận dữ liệu từ server
        String position = din.readUTF();
        System.out.println(position);

        client.close();
    }
}
