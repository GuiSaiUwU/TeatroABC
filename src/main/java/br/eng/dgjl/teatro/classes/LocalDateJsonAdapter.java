package br.eng.dgjl.teatro.classes;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateJsonAdapter extends TypeAdapter<LocalDate> {
    DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    @Override public void write(JsonWriter out, LocalDate data) throws IOException {
        data.format(formato);
        out.beginObject();
        out.name("dataNascimento");
        out.value(data.toString());
        out.endObject();
    }

    @Override public LocalDate read(JsonReader in) throws IOException {
        in.beginObject();
        in.nextName();
        LocalDate data = LocalDate.parse(in.nextString(), formato);
        in.endObject();
        return data;
    }
}