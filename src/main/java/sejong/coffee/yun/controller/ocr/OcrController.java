package sejong.coffee.yun.controller.ocr;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

@RestController
@RequestMapping("/ocr")
public class OcrController {
    private final String BASE_URL = "https://zfusrxrk3k.apigw.ntruss.com/custom/v1/23890/2453eb775e796913aca6d68e93eb5c9d0eda8b9e8f224fe7413ac067ae57fc7b/document/credit-card";

    @GetMapping("/v2")
    public ResponseEntity<String> creditCardOCR() {
        String secretKey = "bHdFSFJGTklOVVJzUFBNeEx3UmhFU3dZdXVtakxUY3U=";
        String responseBody = "";
        try {
            URL url = new URL(BASE_URL);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setUseCaches(false);
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            con.setRequestProperty("X-OCR-SECRET", secretKey);

            JSONObject json = new JSONObject();
            json.put("version", "V2");
            json.put("requestId", UUID.randomUUID().toString());
            json.put("timestamp", System.currentTimeMillis());
            JSONObject image = new JSONObject();
            image.put("format", "png");
//            image.put("url", "https://kr.object.ncloudstorage.com/ocr-ci-test/sample/1.jpg"); // image should be public, otherwise, should use data
            FileInputStream inputStream = new FileInputStream("/Users/hayoon/Downloads/ocrtest/src/main/resources/static/images/img_1.png");
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();
            image.put("data", buffer);
            image.put("name", "demo");
            JSONArray images = new JSONArray();
            images.put(image);
            json.put("images", images);
            String postParams = json.toString();

            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(postParams);
            wr.flush();
            wr.close();

            int responseCode = con.getResponseCode();
            BufferedReader br;
            if (responseCode == 200) {
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else {
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close();

            responseBody = String.valueOf(response);
        } catch (Exception e) {
            System.out.println(e);
        }

        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }
}
