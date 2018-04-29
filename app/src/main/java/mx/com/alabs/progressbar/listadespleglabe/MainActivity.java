package mx.com.alabs.progressbar.listadespleglabe;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import component.ListGroup;
import component.OptionView;

public class MainActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        ((ListGroup)findViewById(R.id.ayuda)).setOnClickListener(new ListGroup.OnSelectOption() {
            @Override
            public void OnSelect(ListGroup listGroup, OptionView option, int optionId) {
            
            }
        });
    }
}
