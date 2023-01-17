package io.github.cunnydevelopment.cunnyaddon.utility;

import io.github.cunnydevelopment.cunnyaddon.Cunny;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class StringUtils {
    private static final Base64.Encoder encoder = Base64.getEncoder();
    private static final Pattern findTag = Pattern.compile("<(rwg|rwng|rnwg|rng|rtoken|rip|vploit|rng:\\d+|disconnect|toggle:[\\w+ ]+|toggle:[\\w+ ]+:(on|off))>");
    private static final Random random = new Random(System.currentTimeMillis());
    private static final List<String> wordList = new ArrayList<>();
    private static final Map<String, String> owoMap = new HashMap<>();
    private static Long worldListStart = 0L;

    public static void init() {
        new Thread(StringUtils::loadWordList).start();
        owoMap.putAll(Map.of("hacker", "haxor", "hacks", "hax", "ch", "chw", "Qu", "Qwu", "qu", "qwu"));
        owoMap.putAll(Map.of("r", "w", "l", "w", "R", "W", "L", "W", "no", "nu", "has", "haz", "have", "haz", "you", "uu", "the ", "da ", "The ", "Da "));
    }

    public static String owoify(String str) {
        var ref = new Object() {
            String ph = str;
        };
        owoMap.forEach((s, s2) -> {
            if (ref.ph.contains(s)) ref.ph = ref.ph.replaceAll(s, s2);
        });
        return ref.ph;
    }

    public static void loadWordList() {
        if (worldListStart == 0) worldListStart = System.nanoTime();

        if (FileSystem.exists(FileSystem.CUNNY_PATH + "words.txt")) {
            wordList.addAll(List.of(FileSystem.read(FileSystem.CUNNY_PATH + "words.txt").split("\n")));
            Cunny.LOG.info("Word List {}ns to load", System.nanoTime() - worldListStart);
        } else {
            FileSystem.writeUrl("https://raw.githubusercontent.com/dwyl/english-words/master/words.txt", FileSystem.CUNNY_PATH + "words.txt");
            loadWordList();
        }

    }

    public static String removeColors(String str) {
        return str.replaceAll("§[a-z0-9]", "");
    }

    public static String readable(String ostr) {
        StringBuilder nstr = new StringBuilder();
        if (ostr.contains("-")) {
            for (String str : ostr.split("-")) {
                String first = str.split("")[0];
                nstr.append(str.replaceFirst(first, first.toUpperCase(Locale.ROOT))).append("-");
            }
        } else {
            nstr = new StringBuilder(ostr.toLowerCase(Locale.ROOT));
            String first = nstr.toString().split("")[0];
            nstr = new StringBuilder(nstr.toString().replaceFirst(first, first.toUpperCase(Locale.ROOT)));
        }
        nstr = new StringBuilder(nstr.toString().trim().replaceAll("-", " "));

        return nstr.toString();
    }


    public static String purifyText(String str) {
        String newString = str;

        Matcher matcher = findTag.matcher(newString);

        while (matcher.find()) {
            String caseStr = matcher.group();
            String[] args = caseStr.replaceFirst("<", "").replaceFirst(">", "").split(":");
            switch (args[0].toLowerCase()) {
                case "toggle" -> {
                    if (args.length >= 2) {
                        for (Module module : Modules.get().getAll()) {
                            if (module.name.equalsIgnoreCase(args[1]) || module.title.equalsIgnoreCase(args[1])) {
                                if (args.length == 3) {
                                    if (args[2].equals("on") && !module.isActive()) module.toggle();
                                    else if (args[2].equals("off") && module.isActive()) module.toggle();
                                } else {
                                    module.toggle();
                                }
                            }
                        }
                    }
                    newString = newString.replaceFirst(caseStr, "");
                }
                case "disconnect" -> {
                    assert mc.player != null;
                    mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, null));
                    return "";
                }
                case "rwg" ->
                    newString = newString.replaceFirst(caseStr, !wordList.isEmpty() ? Objects.requireNonNull(ArrayUtils.random(wordList)) : "word");
                case "rng" -> {
                    if (args.length == 2) {
                        try {
                            String numStr = caseStr.split(":")[1];
                            boolean min = numStr.startsWith("-");
                            numStr = numStr.replaceAll("-", "");
                            double max = Double.parseDouble(numStr);
                            newString = newString.replaceFirst(caseStr, random.nextBoolean() && min ? "-" : "" + Math.floor(random.nextDouble(max)) + "");
                        } catch (Exception e) {
                            newString = newString.replaceFirst(caseStr, Math.floor(random.nextDouble(1000000)) + "");
                        }
                    } else {
                        newString = newString.replaceFirst(caseStr, Math.floor(random.nextDouble(1000000)) + "");
                    }
                }
                case "rnwg", "rwng" ->
                    newString = newString.replaceFirst(caseStr, random.nextBoolean() && !wordList.isEmpty() ? Objects.requireNonNull(ArrayUtils.random(wordList)) : random.nextInt(1000000) + "");
                case "rtoken" -> newString = newString.replaceFirst(caseStr, discordTokenGenerator());
                case "rip" -> newString = newString.replaceFirst(caseStr, ipGenerator());
                case "vploit" -> newString = newString.replaceFirst(caseStr, "https://github.com/ViaTi");
            }
        }
        return newString.strip();
    }

    public static String ipGenerator() {
        int selection = random.nextInt(12);
        switch (selection) {
            case 1 -> {
                //11.0.0.0-100.63.255.255
                int pos1 = random.nextInt(11, 100);
                int pos2 = random.nextInt(255);
                if (pos1 == 100) pos2 = random.nextInt(63);
                return pos1 + "." + pos2 + "." + random.nextInt(255) + "." + random.nextInt(255);
            }
            case 2 -> {
                //100.128.0.0-126.255.255.255
                int pos1 = random.nextInt(100, 126);
                int pos2 = random.nextInt(255);
                if (pos1 == 100) random.nextInt(128, 255);
                return pos1 + "." + pos2 + "." + random.nextInt(255) + "." + random.nextInt(255);
            }
            case 3 -> {
                //128.0.0.0-169.253.255.255
                int pos1 = random.nextInt(128, 169);
                int pos2 = random.nextInt(255);
                if (pos1 == 169) pos2 = random.nextInt(253);
                return pos1 + "." + pos2 + "." + random.nextInt(255) + "." + random.nextInt(255);
            }
            case 4 -> {
                //169.255.0.0-172.15.255.255
                int pos1 = random.nextInt(128, 169);
                int pos2 = random.nextInt(255);
                if (pos1 == 169) pos2 = 255;
                else if (pos1 == 172) pos2 = random.nextInt(15);
                return pos1 + "." + pos2 + "." + random.nextInt(255) + "." + random.nextInt(255);
            }
            case 5 -> {
                //172.32.0.0-191.255.255.255
                int pos1 = random.nextInt(172, 191);
                int pos2 = random.nextInt(255);
                if (pos1 == 172) pos2 = random.nextInt(32, 255);
                return pos1 + "." + pos2 + "." + random.nextInt(255) + "." + random.nextInt(255);
            }
            case 6 -> {
                //192.0.3.0-192.88.98.255
                int pos1 = 192;
                int pos2 = random.nextInt(88 + 1);
                int pos3 = random.nextInt(255);
                if (pos2 == 88) pos3 = random.nextInt(99);
                if (pos2 == 0) pos3 = random.nextInt(3, 255);
                return pos1 + "." + pos2 + "." + pos3 + "." + random.nextInt(255);
            }
            case 7 -> {
                //192.88.100.0-192.167.255.255
                int pos1 = 192;
                int pos2 = random.nextInt(88, 167);
                int pos3 = random.nextInt(255);
                if (pos2 == 88) pos3 = random.nextInt(100, 255);
                return pos1 + "." + pos2 + "." + pos3 + "." + random.nextInt(255);
            }
            case 8 -> {
                //192.169.0.0-198.17.255.255
                int pos1 = random.nextInt(192, 198);
                int pos2 = random.nextInt(255);
                if (pos1 == 192) pos2 = random.nextInt(169, 255);
                if (pos1 == 198) pos2 = random.nextInt(17);
                return pos1 + "." + pos2 + "." + random.nextInt(255) + "." + random.nextInt(255);
            }
            case 9 -> {
                //198.20.0.0-198.51.99.255
                int pos1 = 198;
                int pos2 = random.nextInt(20, 51);
                int pos3 = random.nextInt(255);
                if (pos2 == 51) pos3 = random.nextInt(99);
                return pos1 + "." + pos2 + "." + pos3 + "." + random.nextInt(255);
            }
            case 10 -> {
                //198.51.101.0-203.0.112.255
                int pos1 = random.nextInt(198, 203);
                int pos2 = random.nextInt(255);
                int pos3 = random.nextInt(255);
                if (pos1 == 198) {
                    pos2 = random.nextInt(51, 255);
                    if (pos2 == 51) pos3 = random.nextInt(101, 255);
                }

                if (pos1 == 203) {
                    pos2 = 0;
                    pos3 = random.nextInt(112);
                }
                return pos1 + "." + pos2 + "." + pos3 + "." + random.nextInt(255);
            }
            case 11 -> {
                //203.0.114.0-223.255.255.255
                int pos1 = random.nextInt(203, 223);
                int pos2 = random.nextInt(20, 51);
                int pos3 = random.nextInt(255);
                if (pos1 == 203 && pos2 == 0) pos3 = random.nextInt(114, 255);
                return pos1 + "." + pos2 + "." + pos3 + "." + random.nextInt(255);
            }
            default -> {
                //1.0.0.0-9.255.255.255
                return random.nextInt(9) + "." + random.nextInt(255) + "." + random.nextInt(255) + "." + random.nextInt(255);
            }
        }
    }

    public static String discordTokenGenerator() {
        String firstHalf = encoder.encodeToString(randomInt(18).getBytes());
        return firstHalf + "." + randomText(6) + "." + randomText(27);
    }

    public static String randomText(int amount) {
        return randomText(amount, false);
    }

    public static String randomText(int amount, boolean uni) {
        StringBuilder str = new StringBuilder();
        int leftLimit = 48;
        int rightLimit = 122;
        if (uni) {
            leftLimit = 123;
            rightLimit = 20000;
        }


        for (int i = 0; i < amount; i++) {
            str.append((char) (leftLimit + (int) (random.nextFloat() * (rightLimit - leftLimit + 1))));
        }
        return str.toString();
    }

    public static String randomInt(int amount) {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < amount; i++) {
            str.append(random.nextInt(9));
        }
        return str.toString();
    }
}
