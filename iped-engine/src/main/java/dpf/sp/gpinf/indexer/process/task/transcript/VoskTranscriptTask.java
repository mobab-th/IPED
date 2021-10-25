package dpf.sp.gpinf.indexer.process.task.transcript;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import javax.sound.sampled.AudioSystem;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.vosk.Model;
import org.vosk.Recognizer;

import dpf.sp.gpinf.indexer.Configuration;
import dpf.sp.gpinf.indexer.config.ConfigurationManager;

public class VoskTranscriptTask extends AbstractTranscriptTask {

    private static Model model;

    private Recognizer recognizer;

    @Override
    public void init(ConfigurationManager configurationManager) throws Exception {

        super.init(configurationManager);

        if (!this.isEnabled()) {
            return;
        }

        if (model == null) {
            model = new Model(new File(Configuration.getInstance().appRoot, "models/vosk").getAbsolutePath());
        }

        recognizer = new Recognizer(model, 16000);
        recognizer.setWords(true);

    }

    @Override
    public void finish() throws Exception {
        super.finish();
        if (this.isEnabled()) {
            recognizer.close();
            if (model != null) {
                model.close();
                model = null;
            }
        }
    }

    @Override
    protected TextAndScore transcribeWav(File tmpFile) throws Exception {

        TextAndScore textAndScore = null;
        recognizer.reset();

        try (InputStream ais = AudioSystem.getAudioInputStream(tmpFile)) {

            StringBuilder totalText = new StringBuilder();
            double totalScore = 0;
            int words = 0;

            int nbytes;
            byte[] buf = new byte[1 << 20];
            while ((nbytes = ais.read(buf)) >= 0) {
                if (recognizer.acceptWaveForm(buf, nbytes)) {
                    TextScoreWords result = decodeFromJson(recognizer.getResult());
                    if (result != null) {
                        totalText.append(result.text);
                        totalScore += result.score;
                        words += result.words;
                    }
                } else {
                    // System.out.println(recognizer.getPartialResult());
                }
            }

            TextScoreWords result = decodeFromJson(recognizer.getFinalResult());
            if (result != null) {
                totalText.append(result.text);
                totalScore += result.score;
                words += result.words;
            }

            if (words > 0) {
                textAndScore = new TextAndScore();
                textAndScore.text = totalText.toString().trim();
                textAndScore.score = totalScore / words;
            }
        }

        return textAndScore;
    }

    private TextScoreWords decodeFromJson(String json) throws ParseException {
        String str = new String(json.getBytes(), StandardCharsets.UTF_8);
        JSONParser parser = new JSONParser();
        JSONObject root = (JSONObject) parser.parse(str);
        JSONArray array = (JSONArray) root.get("result");
        if (array == null || array.size() == 0) {
            return null;
        }

        double totScore = 0;
        int words = array.size();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < words; i++) {
            JSONObject obj = (JSONObject) array.get(i);
            double score = (Double) obj.get("conf");
            if (score >= transcriptConfig.getMinWordScore()) {
                text.append(obj.get("word")).append(" ");
            } else {
                text.append("* ");
            }
            totScore += score;
        }

        TextScoreWords result = new TextScoreWords();
        result.text = text.toString();
        result.score = totScore;
        result.words = words;
        return result;
    }

    private static class TextScoreWords extends TextAndScore {
        int words = 0;
    }

}