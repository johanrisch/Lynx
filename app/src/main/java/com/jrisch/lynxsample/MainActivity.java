package com.jrisch.lynxsample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.jrisch.lynx.Lynx;
import com.jrisch.lynx.LynxDiskStorage;
import com.jrisch.lynx_android.LynxObjectMapper;
import com.jrisch.lynx_android.cipher.LynxBouncyCastleProvider;
import com.jrisch.lynx.LynxCipherHandler;
import com.jrisch.lynx_android.cipher.LynxDefaultPasswordSupplier;
import com.jrisch.lynx_android.storage.LynxExternalStorageInfo;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.key)
    EditText key;

    @Bind(R.id.value)
    EditText value;

    @Bind(R.id.data)
    TextView data;

    private LynxDiskStorage<String, String> secureLynxStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.secureLynxStorage = Lynx.create(new LynxExternalStorageInfo(this), String.class, String.class)
                                     .withMapper(new LynxObjectMapper())
                                     .withCipherHandler(new LynxCipherHandler(new LynxDefaultPasswordSupplier(this), new LynxBouncyCastleProvider(), new LynxBase64Impl()))
                                     .named("testSecureStorage")
                                     .build();
        ButterKnife.bind(this);
    }

    @OnClick(R.id.submit)
    public void onSubmit() {
        secureLynxStorage.set(key.getText().toString(), value.getText().toString());
        key.setText("");
        value.setText("");
    }

    @OnClick(R.id.load)
    public void onLoad() {
        if (key.getText().length() > 0) {
            data.append("\n" + secureLynxStorage.get(key.getText().toString()));
        } else {
            List<String> loadedData = secureLynxStorage.getAll();
            data.setText("");
            for (String s : loadedData) {
                data.append(s + "\n");
            }
        }
    }
}
