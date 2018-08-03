import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {


    public static void main(String[] args) {
        if (args.length > 0) {
            Stream<String> inputFileStream = getInputFileStream(args[0]);

            if (inputFileStream == null) {
                System.out.println("Failed to find given file");
                return;
            }

            LinkedHashMap<String, LinkedList<String>> pinfallQueueHashMap = generatePinfallQueues(inputFileStream);
            LinkedList<String> playerList = new LinkedList<>(pinfallQueueHashMap.keySet());
            LinkedHashMap<String, LinkedList<Integer>> frameTotalQueuesHashmap = generateFrameTotalQueues(pinfallQueueHashMap, playerList);
            outputScores(pinfallQueueHashMap, frameTotalQueuesHashmap, playerList);
        }
    }

    private static Stream<String> getInputFileStream(final String filePath) {

        Stream<String> fileStream = null;
        try {
            fileStream = Files.lines(Paths.get(filePath));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileStream;
    }

    private static LinkedHashMap<String, LinkedList<String>> generatePinfallQueues(final Stream<String> pinfallFileStream) {
        LinkedHashMap<String, LinkedList<String>> pinfallQueueHashMap = new LinkedHashMap<>();

        pinfallFileStream.forEach(line -> {
            String[] splitLine = line.split(" ");
            String name = splitLine[0];
            String score = splitLine[1];

            LinkedList<String> pinfallQueue = new LinkedList<>();
            pinfallQueue.add(score);

            pinfallQueueHashMap.merge(name, pinfallQueue, (pinfallQueue1, pinfallQueue2) ->
                    Stream.of(pinfallQueue1, pinfallQueue2)
                            .flatMap(List::stream)
                            .collect(Collectors.toCollection(LinkedList::new)));
        });

        return pinfallQueueHashMap;
    }


    private static LinkedHashMap<String, LinkedList<Integer>> generateFrameTotalQueues(final LinkedHashMap<String, LinkedList<String>> pinfallQueueHashMap,
                                                                                       final LinkedList<String> playerList) {
        LinkedHashMap<String, LinkedList<Integer>> frameTotalsQueueHashMap = new LinkedHashMap<>();

        playerList.forEach(currentPlayer -> {
            LinkedList<Integer> pinfallQueueCopy = pinfallQueueHashMap.get(currentPlayer)
                    .stream()
                    .map(value -> {
                        if (value.equals("F"))
                            value = "0";
                        return Integer.valueOf(value);
                    }).collect(Collectors.toCollection(LinkedList::new));

            LinkedList<Integer> frameTotalQueue = new LinkedList<>();
            Integer runningTotal = 0;
            for (int i = 0; i < 10; i++) {
                int frameSum = 0;
                frameSum += pinfallQueueCopy.pop();

                if (frameSum < 10) {
                    frameSum += pinfallQueueCopy.pop();
                    if (frameSum == 10) {
                        frameSum += pinfallQueueCopy.get(0);
                    }
                } else if (frameSum == 10) {
                    frameSum += pinfallQueueCopy.get(0);
                    frameSum += pinfallQueueCopy.get(1);
                }

                runningTotal += frameSum;
                frameTotalQueue.add(runningTotal);
            }

            frameTotalsQueueHashMap.put(currentPlayer, frameTotalQueue);
        });
        return frameTotalsQueueHashMap;
    }

    private static void outputScores(final HashMap<String, LinkedList<String>> pinfallQueueHashmap,
                                     final HashMap<String, LinkedList<Integer>> frameTotalQueueHashmep,
                                     final LinkedList<String> playerList) {
        System.out.print("Frame\t\t");
        for (int i = 1; i < 11; i++) {
            System.out.print(String.valueOf(i) + "\t\t");
        }
        System.out.println();

        while (!pinfallQueueHashmap.isEmpty()) {
            String currentPlayer = playerList.remove();
            LinkedList<String> pinfallQueue = pinfallQueueHashmap.remove(currentPlayer);
            LinkedList<Integer> frameTotalQueue = frameTotalQueueHashmep.remove(currentPlayer);

            System.out.println(currentPlayer);
            System.out.print("Pinfalls\t");

            Integer frame = 1;
            while (!pinfallQueue.isEmpty()) {
                String pinfall = pinfallQueue.pop();
                Integer frameTotal = pinfall.equals("F") ? 0 : Integer.valueOf(pinfall);

                if (frameTotal < 10) {
                    System.out.print(pinfall + "\t");
                    pinfall = pinfallQueue.pop();
                    frameTotal += pinfall.equals("F") ? 0 : Integer.valueOf(pinfall);

                    if (frameTotal == 10) {
                        System.out.print("/\t");
                    } else {
                        System.out.print(pinfall + "\t");
                    }

                } else {
                    if (frame < 10) {
                        System.out.print("\tX\t");
                    } else {
                        System.out.print("X\t");
                    }
                }

                frame++;
            }
            System.out.println();

            System.out.print("Score");
            while (!frameTotalQueue.isEmpty()) {
                System.out.print("\t\t" + String.valueOf(frameTotalQueue.pop()));
            }
            System.out.println();

        }
    }
}
