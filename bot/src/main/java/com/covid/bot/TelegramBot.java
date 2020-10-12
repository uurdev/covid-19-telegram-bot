package com.covid.bot;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class TelegramBot extends TelegramLongPollingBot {

    OkHttpClient okHttpClient;
    Request request;
    Response response = null;
    JSONObject jsonObject;

    String welcomeMessage =
            "Thank you for using Covid-19 World Status Bot \n" +
                    "If you are curious about which country, just write the name of the country, if you are curious about the result of the whole world, just write 'world'. \n" +
                    "Example : 'Turkey' or 'World' \n" +
                    "Anyone can see the codes that made me \n" +
                    "Do you want to write code to make me better? \n " +
                    "If your answer is 'yes' i am here : https://github.com/uurdev/covid-19-telegram-bot \n" +
                    "Developer : https://github.com/uurdev \n";


    @Override
    public void onUpdateReceived(Update update) {
        SendMessage sendMessage = new SendMessage();

        if (update.getMessage().getText().equals("/start") || update.getMessage().getText().equals("Back") || update.getMessage().getText().equals("/start@Covid19StatusBot")) {
            ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
            sendMessage.setReplyMarkup(replyKeyboardMarkup);
            sendMessage.setText("Hi " + update.getMessage().getFrom().getFirstName() + ",\n\n" + welcomeMessage);
            try {
                sendMessage.setChatId(update.getMessage().getChatId());
                execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        } else {
            try {
                String country = update.getMessage().getText().toLowerCase();

                if (country.equals("world")) {
                    okHttpClient = new OkHttpClient();
                    request = new Request.Builder()
                            .url("https://disease.sh/v2/all")
                            .get()
                            .build();
                    Response response = okHttpClient.newCall(request).execute();
                    String data = response.body().string();

                    sendMessage.setText(covidResultForWorld(data));
                    sendMessage.setChatId(update.getMessage().getChatId());
                    execute(sendMessage);
                } else {
                    okHttpClient = new OkHttpClient();
                    request = new Request.Builder()
                            .url("https://disease.sh/v3/covid-19/countries/" + country)
                            .get()
                            .build();
                    response = okHttpClient.newCall(request).execute();
                    String data = response.body().string();

                    sendMessage.setText(covidResultForCountry(data, country));
                    sendMessage.setChatId(update.getMessage().getChatId());
                    execute(sendMessage);


                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private String covidResultForWorld(String data) throws ParseException {
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParser.parse(data);
        String result = "COVID 19 World  DATA\n\nTotal cases : " + jsonObject.get("cases") +
                "\nRecovered : " + jsonObject.get("recovered") +
                "\nCritical : " + jsonObject.get("critical") +
                "\nActive : " + jsonObject.get("active") +
                "\nToday Cases : " + jsonObject.get("todayCases") +
                "\nTotal Deaths : " + jsonObject.get("deaths") +
                "\nToday Deaths : " + jsonObject.get("todayDeaths");
        return result;
    }

    private String covidResultForCountry(String data, String country) throws ParseException {
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParser.parse(data);
        String result = "COVID 19 " + country + " DATA\n\nTotal cases : " + jsonObject.get("cases") +
                "\nRecovered : " + jsonObject.get("recovered") +
                "\nCritical : " + jsonObject.get("critical") +
                "\nActive : " + jsonObject.get("active") +
                "\nToday Cases : " + jsonObject.get("todayCases") +
                "\nTotal Deaths : " + jsonObject.get("deaths") +
                "\nToday Deaths : " + jsonObject.get("todayDeaths");
        return result;
    }

    @Override
    public String getBotUsername() {
        return "Covid19StatusBot";
    }

    @Override
    public String getBotToken() {
        return "1375924809:AAGriKvun0ZEaJcMIEsiMN2DkUqvArk_7Kw";
    }
}
