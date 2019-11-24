package util;

import model.BalanceRow;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * класс, содержащий методы чтения и записи файлов
 */
public class FileUtil {

    /**
     * имя файла, хранящего список доходов и расходов
     */
    private static final String FILENAME = "balance.fba";

    /**
     * метод чтения данных из файла
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static ArrayList<BalanceRow> readBalanceRows() throws IOException, ClassNotFoundException {
        File file = new File(FILENAME);
        if (file.exists()) {
            FileInputStream fileInputStream = new FileInputStream(FILENAME);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            ArrayList<BalanceRow> balanceRows = (ArrayList) objectInputStream.readObject();
            return balanceRows;
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * метод записи данных в файл
     * @param balanceRowList
     * @throws IOException
     */
    public static void writeBalanceRows(ArrayList<BalanceRow> balanceRowList) throws IOException {
        File file = new File(FILENAME);
        if(!file.exists()){
            file.createNewFile();
        }
        FileOutputStream fileOutputStream = new FileOutputStream(FILENAME);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(balanceRowList);
        objectOutputStream.close();
        fileOutputStream.close();
    }
}
