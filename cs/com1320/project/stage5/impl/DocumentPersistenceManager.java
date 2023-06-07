package edu.yu.cs.com1320.project.stage5.impl;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import edu.yu.cs.com1320.project.stage5.Document;
import edu.yu.cs.com1320.project.stage5.PersistenceManager;
import jakarta.xml.bind.DatatypeConverter;

import java.io.*;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * created by the document store and given to the BTree via a call to BTree.setPersistenceManager
 */
public class DocumentPersistenceManager implements PersistenceManager<URI, Document> {
    private File dir;

    public DocumentPersistenceManager(File baseDir){
        if (baseDir != null) {
            this.dir = baseDir;
        }
        else {
            String userDir = System.getProperty(("user.dir"));
            this.dir = new File(userDir);
        }
    }

    @Override
    public void serialize(URI uri, Document val) throws IOException {
        if (uri == null) {
            throw new IllegalArgumentException();
        }
        if (val == null) {
            throw new IllegalArgumentException();
        }
        //Text Document Serialization
        if (val.getDocumentTxt() != null) {
            JsonSerializer<Document> gsonTextDocument = new JsonSerializer<Document>() {
                @Override
                public JsonElement serialize(Document document, Type type, JsonSerializationContext jsonSerializationContext) {
                    JsonObject jsonObj = new JsonObject();
                    jsonObj.addProperty("uri", document.getKey().toString());
                    jsonObj.addProperty("text", document.getDocumentTxt());
                    JsonElement jsonElement = new Gson().toJsonTree(document.getWordMap());
                    jsonObj.add("map", jsonElement);
                    return jsonObj;
                }
            };
            JsonElement jsonText = gsonTextDocument.serialize(val, null, null);
            Gson gson = new Gson();
            String jsonTextString =  gson.toJson(jsonText);
            String newUri = "";
            if (uri.getScheme() != null) {
                String host = uri.getHost();
                String path = uri.getPath();
                newUri = host + path + ".json";
            }
            else {
                newUri = uri.toString();
            }
            File file = new File(this.dir, newUri);
            file.getParentFile().mkdirs();
            try {
                FileWriter fw = new FileWriter(file);
                fw.write(jsonTextString);
                fw.close();
            }
            catch (IOException e){
                throw new IOException(e.getMessage());
            }
        }
        //Binary Document Serialization
        else if (val.getDocumentBinaryData() != null) {
            JsonSerializer<Document> gsonBinaryDataDocument = new JsonSerializer<Document>() {
                @Override
                public JsonElement serialize(Document document, Type type, JsonSerializationContext jsonSerializationContext) {
                    JsonObject jsonObj = new JsonObject();
                    jsonObj.addProperty("uri", document.getKey().toString());
                    //serialize the binary data of the document
                    jsonObj.addProperty("binaryData", DatatypeConverter.printBase64Binary(val.getDocumentBinaryData()));
                    return jsonObj;
                }
            };
            JsonElement jsonBinary = gsonBinaryDataDocument.serialize(val, null, null);
            Gson gson = new Gson();
            String jsonBinaryString =  gson.toJson(jsonBinary);
            String newUri = "";
            if (uri.getScheme() != null)  {
                String host = uri.getHost();
                String path = uri.getPath();
                newUri = host + path + ".json";
            }
            else {
                newUri = uri.toString();
            }
            File file = new File(this.dir, newUri);
            file.getParentFile().mkdirs();
            try {
                FileWriter fw = new FileWriter(file);
                fw.write(jsonBinaryString);
                fw.close();
            }
            catch (IOException e){
                throw new IOException();
            }
        }
    }

    @Override
    public Document deserialize(URI uri) throws IOException {
        if (uri == null) {
            throw new IllegalArgumentException();
        }
        Gson gson = new Gson();
        String newUri = "";
        if (uri.getScheme() != null)  {
            String host = uri.getHost();
            String path = uri.getPath();
            newUri = host + path + ".json";
        }
        else {
            newUri = uri.toString();
        }
        File file = new File(this.dir, newUri);
        String fileName = file.toString();
        String jsonFileName = gson.toJson(fileName);
        FileReader reader = new FileReader(file);
        JsonElement jsonElement = gson.fromJson(reader, JsonElement.class);
        reader.close();
        JsonDeserializer<Document> jsonDeserializer = new JsonDeserializer<Document>() {
            @Override
            public Document deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
                JsonObject jsonObj = jsonElement.getAsJsonObject();
                String uriAsString =  jsonObj.get("uri").getAsString();
                URI uri = null;
                try {
                    uri = new URI(uriAsString);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e.getMessage());
                }
                String documentText = null;
                try {
                    documentText = jsonObj.get("text").getAsString();
                }
                catch (Exception e) {
                    //if an exception is thrown this must be a Binary Document
                    String binaryData  = jsonObj.get("binaryData").getAsString();
                    byte[] base64Decoded = DatatypeConverter.parseBase64Binary(binaryData);
                    return new DocumentImpl(uri, base64Decoded);
                }
                JsonObject map  = jsonObj.get("map").getAsJsonObject();;
                HashMap<String, Integer> wordCountMap = gson.fromJson(map, HashMap.class);
                return new DocumentImpl(uri, documentText, wordCountMap);
            }
        };
        Document deserializedDoc = jsonDeserializer.deserialize(jsonElement, null, null);
        return deserializedDoc;
    }

    @Override
    public boolean delete(URI uri) throws IOException {
        String newUri = "";
        if (uri.getScheme() != null)  {
            String host = uri.getHost();
            String path = uri.getPath();
            newUri = host + path + ".json";
        }
        else {
            newUri = uri.toString();
        }
        File file = new File(this.dir, newUri);
        return file.delete();
    }
}