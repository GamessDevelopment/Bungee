package net.zerox.github.api;

import com.google.gson.stream.JsonReader;
import com.google.gson.internal.bind.TypeAdapters;
import com.google.gson.stream.JsonWriter;
import com.google.common.io.BaseEncoding;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.awt.image.BufferedImage;
import lombok.NonNull;
import com.google.gson.TypeAdapter;

public class Favicon
{
    private static final TypeAdapter<Favicon> FAVICON_TYPE_ADAPTER;
    @NonNull
    private final String encoded;
    
    public static TypeAdapter<Favicon> getFaviconTypeAdapter() {
        return Favicon.FAVICON_TYPE_ADAPTER;
    }
    
    public static Favicon create(final BufferedImage image) {
        if (image.getWidth() != 64 || image.getHeight() != 64) {
            throw new IllegalArgumentException("Server icon must be exactly 64x64 pixels");
        }
        byte[] imageBytes;
        try {
            final ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ImageIO.write(image, "PNG", stream);
            imageBytes = stream.toByteArray();
        }
        catch (IOException e) {
            throw new AssertionError((Object)e);
        }
        final String encoded = "data:image/png;base64," + BaseEncoding.base64().encode(imageBytes);
        if (encoded.length() > 32767) {
            throw new IllegalArgumentException("Favicon file too large for server to process");
        }
        return new Favicon(encoded);
    }
    
    @Deprecated
    public static Favicon create(final String encodedString) {
        return new Favicon(encodedString);
    }
    
    private Favicon(@NonNull final String encoded) {
        if (encoded == null) {
            throw new NullPointerException("encoded is marked non-null but is null");
        }
        this.encoded = encoded;
    }
    
    @NonNull
    public String getEncoded() {
        return this.encoded;
    }
    
    static {
        FAVICON_TYPE_ADAPTER = new TypeAdapter<Favicon>() {
            @Override
            public void write(final JsonWriter out, final Favicon value) throws IOException {
                TypeAdapters.STRING.write(out, (value == null) ? null : value.getEncoded());
            }
            
            @Override
            public Favicon read(final JsonReader in) throws IOException {
                final String enc = TypeAdapters.STRING.read(in);
                return (enc == null) ? null : Favicon.create(enc);
            }
        };
    }
}
