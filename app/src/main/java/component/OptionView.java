package component;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.SoundEffectConstants;
import android.widget.LinearLayout;
import android.widget.TextView;

import mx.com.alabs.progressbar.listadespleglabe.R;

/**
 * Created by msarabia on 3/12/18.
 */

public class OptionView extends LinearLayout {
    
    //region Interface and setter listerner
    private OnClickLister listener;
    
    public void setListener(OnClickLister listener) {
        this.listener = listener;
    }
    
    //    interface
    public interface OnClickLister {
        public void onClick(OptionView option, @IdRes int selectId);
    }
    //endregion
    
    //region Constructores
    public OptionView(Context context) {
        this(context, null);
    }
    
    public OptionView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (getImportantForAutofill() == IMPORTANT_FOR_AUTOFILL_AUTO) {
                setImportantForAutofill(IMPORTANT_FOR_AUTOFILL_YES);
            }
        }
        
        setOrientation(VERTICAL);
        
        inflate(context, R.layout.action_item, this);
        TextView title = (TextView) findViewById(R.id.title);
        TextView description = (TextView) findViewById(R.id.description);
        AppCompatImageView icon = (AppCompatImageView) findViewById(R.id.icon);
        
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.OptionView);
        
        
        title.setText(attributes.getString(R.styleable.OptionView_title));
        description.setText(attributes.getString(R.styleable.OptionView_description));
        int iconpadding = (int) attributes.getDimension(R.styleable.OptionView_icon_padding, 0.0f);
        int backgroundColor  = attributes.getColor(R.styleable.OptionView_background_icon, -1);
        
        //icono
        int iconImage = (int) attributes.getResourceId(R.styleable.OptionView_icon, -1);
        if (iconImage != -1) {
            Drawable drawable = AppCompatResources.getDrawable(getContext(), iconImage);
            drawable = DrawableCompat.wrap(drawable);
            icon.setImageDrawable(drawable);
            
            if (backgroundColor != -1) {
                // Generamos el Background
                ShapeDrawable bg = new ShapeDrawable(new OvalShape());
                bg.setIntrinsicWidth(drawable.getIntrinsicWidth());
                bg.setIntrinsicHeight(drawable.getIntrinsicHeight());
                bg.getPaint().setColor(backgroundColor);
                icon.setBackgroundDrawable(bg);
            }
            icon.setPadding(iconpadding, iconpadding, iconpadding, iconpadding);
        } else {
            icon.setVisibility(INVISIBLE);
        }
        attributes.recycle();
        
    //    lo hacemso clickeable
        setClickable(true);
    }
    //endregion
    
    
    //Capturamos el evento Click
    @Override
    public boolean performClick() {
        // llamamos al evento del listener
        listener.onClick(this, this.getId());
        
        final boolean handled = super.performClick();
        if (!handled) {
            // View only makes a sound effect if the onClickListener was
            // called, so we'll need to make one here instead.
            playSoundEffect(SoundEffectConstants.CLICK);
        }
        
        return handled;
    }
}
