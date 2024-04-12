package com.example.spotifywrapped;


import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.openai.OpenAiChatModel;

//import com.theokanning.openai-gpt3-java:<api|client|service>:<version>;
public class DescribePersonByMusicTaste {
    private final String apiKey = "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";
    private String artist;
    private String songName;
    private int popularity;
    private String songId;
    private String artistId;
    private String[] artistGenres;
    public DescribePersonByMusicTaste() {

    }

    public String getArtist() {
        return artist;
    }

    public String getDes(String str) {
        String ret = "";
        OpenAiChatModel model = OpenAiChatModel.withApiKey(apiKey);

        ret = model.generate("What da dog doin?");/*String.format(
                "Based on the following summary of my music taste, what kind of person do you think I am?" +
                        " What kind of clothes do you think I would wear:%n%s",str));*/

        return ret;
    }
}
