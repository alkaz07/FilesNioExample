package example.nio;

import java.io.IOException;
import java.net.URL;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousByteChannel;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Function;

public class Main {
    public static void main(String[] args) throws IOException {
        example_blocking_read();
        example_simple_read();
        example_async_read();
    }

    private static void example_async_read() {
        String fname ="a.txt";
        Path path = Paths.get(fname);
        try(var input = AsynchronousFileChannel.open(path)){
            ByteBuffer buffer = ByteBuffer.allocate(100000);
            Future<Integer> futureReadCount = input.read(buffer,0);
            while (!futureReadCount.isDone()){
                System.out.println("читаем...");
            }
            int count = futureReadCount.get();
            System.out.println("count = " + count);
            byte[] resultBytes = new byte[count];
            buffer.get(0, resultBytes);
            String str = new String(resultBytes, StandardCharsets.UTF_8);
            System.out.println("str = " + str);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void example_simple_read() throws IOException {
        String fname ="a.txt";
        Path path = Paths.get(fname);
        List<String> stringList = Files.readAllLines(path, StandardCharsets.UTF_8);
        System.out.println("stringList = " + stringList);
        Files.lines(path, StandardCharsets.UTF_8)
                .sorted(Comparator.reverseOrder())
                .forEach(s-> System.out.println("s = " + s));
        System.out.println("еще раз сортировка без учета регистра");
        Files.lines(path, StandardCharsets.UTF_8)
                .sorted((s1, s2) -> s2.compareToIgnoreCase(s1))
                .forEach(s-> System.out.println("s = " + s));

    }

    private static void example_blocking_read() {
       String fname ="a.txt";
       Path path = Paths.get(fname);
       try(ReadableByteChannel input = FileChannel.open(path)){
           ByteBuffer buffer = ByteBuffer.allocate(100000);
           int readCount = input.read(buffer);
           System.out.println("readCount = " + readCount);           
           byte[] resultBytes = new byte[readCount];
           buffer.get(0, resultBytes);
           String str = new String(resultBytes, StandardCharsets.UTF_8);
           System.out.println("str = " + str);
       }
       catch (IOException e){
           System.out.println(e.getMessage());
       }
    }
}