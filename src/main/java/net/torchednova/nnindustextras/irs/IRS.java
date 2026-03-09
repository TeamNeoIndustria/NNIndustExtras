package net.torchednova.nnindustextras.irs;

import net.minecraft.world.entity.player.Player;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static net.torchednova.nnindustextras.NNIndustExtras.LOGGER;

public class IRS {

    private static final String LINE_END = "\r\n";
    private static final String twoHyphens = "--";
    private static final String boundary = "*****"; // Change this string to a unique boundary
    private static String apiKey;

    public static void NeoIRSInit(String apiKeyNew) {
        apiKey = apiKeyNew;
        //LOGGER.info("key " + apiKey + " loaded");
    }

    public static String getMoney(Player target, int amount, String ref) {
        try {
            URL obj = new URL("https://irs.neonetwork.xyz/api/receive");
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            con.setDoOutput(true);

            try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
                wr.writeBytes(twoHyphens + boundary + LINE_END);
                wr.writeBytes("Content-Disposition: form-data; name=\"apikey\"" + LINE_END);
                wr.writeBytes(LINE_END);
                wr.writeBytes(apiKey + LINE_END);

                wr.writeBytes(twoHyphens + boundary + LINE_END);
                wr.writeBytes("Content-Disposition: form-data; name=\"from\"" + LINE_END);
                wr.writeBytes(LINE_END);
                wr.writeBytes(target.getName().getString() + LINE_END);

                wr.writeBytes(twoHyphens + boundary + LINE_END);
                wr.writeBytes("Content-Disposition: form-data; name=\"amount\"" + LINE_END);
                wr.writeBytes(LINE_END);
                wr.writeBytes(amount + LINE_END);

                wr.writeBytes(twoHyphens + boundary + LINE_END);
                wr.writeBytes("Content-Disposition: form-data; name=\"reference\"" + LINE_END);
                wr.writeBytes(LINE_END);
                wr.writeBytes(ref + LINE_END);

                wr.writeBytes(LINE_END);
                wr.writeBytes(twoHyphens + boundary + twoHyphens + LINE_END);
                wr.flush();
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            in.close();
            LOGGER.info(response.toString());
            return response.toString();
        } catch (Exception exception) {
            LOGGER.error("Unable complete request [{}] [{}] [{}]", target.getName().getString(), amount, ref);
            exception.printStackTrace();
            return null;
        }

    }

    public static String newKey(Player tagret)
    {
        try {
            URL obj = new URL("https://irs.neonetwork.xyz/api/newkey");
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            con.setDoOutput(true);

            try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
                wr.writeBytes(twoHyphens + boundary + LINE_END);
                wr.writeBytes("Content-Disposition: form-data; name=\"apikey\"" + LINE_END);
                wr.writeBytes(LINE_END);
                wr.writeBytes(apiKey + LINE_END);

                wr.writeBytes(twoHyphens + boundary + LINE_END);
                wr.writeBytes("Content-Disposition: form-data; name=\"uuid\"" + LINE_END);
                wr.writeBytes(LINE_END);
                wr.writeBytes(tagret.getName().getString() + LINE_END);


                wr.writeBytes(LINE_END);
                wr.writeBytes(twoHyphens + boundary + twoHyphens + LINE_END);
                wr.flush();
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            in.close();
            LOGGER.info(response.toString());
            return response.toString();
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }

}