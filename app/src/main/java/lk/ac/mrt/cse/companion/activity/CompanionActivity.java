package lk.ac.mrt.cse.companion.activity;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.flipkart.chatheads.ui.ChatHead;
import com.flipkart.chatheads.ui.ChatHeadContainer;
import com.flipkart.chatheads.ui.ChatHeadViewAdapter;
import com.flipkart.chatheads.ui.MaximizedArrangement;
import com.flipkart.chatheads.ui.MinimizedArrangement;

import lk.ac.mrt.cse.companion.R;
import lk.ac.mrt.cse.companion.fragment.LaunchersFragment;

public class CompanionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_companion);

        final ChatHeadContainer chatContainer = (ChatHeadContainer) findViewById(R.id.chat_head_container);
        chatContainer.setViewAdapter(new ChatHeadViewAdapter<String>() {
            @Override
            public FragmentManager getFragmentManager() {
                return getSupportFragmentManager();
            }

            @Override
            public Fragment instantiateFragment(String key, ChatHead chatHead) {
                // return the fragment which should be shown when the arrangment switches to maximized (on clicking a chat head)
                // you can use the key parameter to get back the object you passed in the addChatHead method.
                // this key should be used to decide which fragment to show.
                return new LaunchersFragment();
            }

            @Override
            public Drawable getChatHeadDrawable(String key) {
                // this is where you return a drawable for the chat head itself based on the key. Typically you return a circular shape
                // you may want to checkout circular image library https://github.com/flipkart-incubator/circular-image
                return getResources().getDrawable(R.mipmap.ic_launcher);
            }

            @Override
            public Drawable getPointerDrawable() {
                return null;
            }

            @Override
            public View getTitleView(String key, ChatHead chatHead) {
                return null;
            }
        });

        chatContainer.addChatHead("head0", false,true);
        chatContainer.setArrangement(MaximizedArrangement.class, null);
    }
}
