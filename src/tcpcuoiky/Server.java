/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tcpcuoiky;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author MinhIT
 */
public class Server {

    private static char[][] matrix;
    private static String position;
    public static String decrypt(String data, char[][] matrix) {
        String result = "";
        String[] pairs = convertArray(data);

        for (String pair : pairs) {
            char left = pair.charAt(0);
            int[] left_pos = getPosition(left, matrix);
            char right = pair.charAt(1);
            int[] right_pos = getPosition(right, matrix);

            int left_x, left_y, right_x, right_y;

            if (left_pos[0] == right_pos[0]) { //cùng hàng
                left_x = left_pos[0];
                left_y = (left_pos[1] - 1 + 5) % 5;
                right_x = right_pos[0];
                right_y = (right_pos[1] - 1 + 5) % 5;
            } else if (left_pos[1] == right_pos[1]) { //cùng cột
                left_x = (left_pos[0] - 1 + 5) % 5;
                left_y = left_pos[1];
                right_x = (right_pos[0] - 1 + 5) % 5;
                right_y = right_pos[1];
            } else { //khác hàng khác cột 4 0 2 2 =>  4 2 2 0
                left_x = left_pos[0];
                left_y = right_pos[1];
                right_x = right_pos[0];
                right_y = left_pos[1];
            } 
            position += "\nkí tự: " + matrix[left_x][left_y] + " vị trí: " + left_x + " " + left_y;
            position += "\nkí tự: " + matrix[right_x][right_y] + " vị trí: " + right_x + " " + right_y;
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

    public static String[] convertArray(String message) {
        ArrayList<String> result = new ArrayList<String>();
        int i = 0;
        while (i < message.length()) {
            char c1 = message.charAt(i);
            char c2 = message.charAt(i + 1);
            result.add(new String("" + c1 + c2));
            i += 2;
        }
        return (String[]) result.toArray(new String[0]); //string [] {ea, eb, ec};
    }

    public static String removeX(String chuoi) {
        char[] txt = chuoi.toCharArray();
        for (int i = 0; i < txt.length; i++) {
            if(i% 2 != 0 && txt[i] == 'x' && txt[i-1] != 'x') { 
                txt[i] = '@';
            }
        }
        String str2 = String.valueOf(txt);
        return str2;
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

    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(8888);
        System.out.println("Server đang chạy !!!");
        while (true) {
            Socket ClientConnect = server.accept();
            System.out.println("Có một client kết nối");

            DataInputStream din = new DataInputStream(ClientConnect.getInputStream());
            DataOutputStream dout = new DataOutputStream(ClientConnect.getOutputStream());
            String key = din.readUTF();
            String cipher = din.readUTF();

            matrix = getMatrix(key);
            printMatrix(matrix);
            String cipherDescript = decrypt(cipher, matrix);
            String cipherComplete = removeX(cipherDescript);
            cipherComplete.replaceAll("@", "").trim();
            String sendPosition = position;
            dout.writeUTF(sendPosition);
        }
    }
}
