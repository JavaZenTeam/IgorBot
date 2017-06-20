import java.util.Arrays;
import java.util.Map;

public class YouTouchMyTralala {
    public static void main(String[] args) {
        System.out.println(Arrays.toString("AA OR Bb or cc Or dD".split("(?i)\\s?or\\s?")));


//        HashMap<String, Integer> store = new HashMap<>();
//        store.put("A", 3);
//        store.put("B", 2);
//        store.put("C", 5);
//
//        int combos = store.values().stream().map(i -> i + 1).reduce((a, b) -> a * b).orElse(0);
//        System.out.println("Number of combos: " + combos);
//
//        for (int i = 0; i < combos; i++) {
//            HashMap<String, Integer> combo = new HashMap<>();
//            int j = i;
//            for (Map.Entry<String, Integer> entry : store.entrySet()){
//                int k = entry.getValue() + 1;
//                combo.put(entry.getKey(), j % k);
//                j /= k;
//            }
//
//            System.out.format("%04d: %s\n", i, mapToString(combo));
//        }
    }

    private static String mapToString(Map<String, Integer> map){
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, Integer> entry : map.entrySet())
            for (int i = 0; i < entry.getValue(); i++)
                stringBuilder.append(entry.getKey());
        return stringBuilder.toString();
    }
}