package component;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.concurrent.atomic.AtomicInteger;

import mx.com.alabs.progressbar.listadespleglabe.R;

/**
 * Created by msarabia on 3/12/18.
 */

public class ListGroup extends LinearLayout {
    
    public static final int CLOSED = 1;
    public static final int OPEN   = 2;
    
    private OptionView.OnClickLister mOptionViewListener;
    private OnSelectOption           listener;
    private LinearLayout             container;
    private TextView                 title;
    private TextView                 description;
    private AppCompatImageView       icon;
    private View                     selector;
    
    //region Getter an Setter de estado
    private int state;
    
    public int getState() {
        return state;
    }
    
    public void setState(int state) {
        this.state = state;
        if (state == OPEN) {
            // mostrar la lista
            container.setVisibility(VISIBLE);
            state = OPEN;
            
            //            selector.setBackgroundColor(Color.parseColor("#f5f7f6"));
            selector.setBackground(getResources().getDrawable(R.drawable.bg_listgroup_open));
        } else {
            // contaer la lista
            container.setVisibility(GONE);
            state = CLOSED;
            //            selector.setBackgroundColor(Color.parseColor("#ffffff"));
            selector.setBackground(null);
        }
    }
    
    public boolean isOpen() {
        return state == OPEN;
    }
    //endregion
    
    
    //region Interface
    public interface OnSelectOption {
        public void OnSelect(ListGroup listGroup, OptionView option, @IdRes int optionId);
    }
    
    public void setOnClickListener(OnSelectOption selectOptionListener) {
        listener = selectOptionListener;
    }
    //endregion
    
    
    //region Constructor
    public ListGroup(Context context) {
        super(context);
        setOrientation(VERTICAL);
        init();
    }
    
    
    public ListGroup(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        
        
        // Atributos
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.ListGroup);
        final int  index      = attributes.getInt(R.styleable.ListGroup_orientation, VERTICAL);
        setOrientation(index);
        
        
        init();
        
        // Inicializa los eventos par ael trackin de eventos
        
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        layoutInflater.inflate(R.layout.component_listgroup, this, true);
        
        container = findViewById(R.id.container);
        title = (TextView) findViewById(R.id.title);
        description = (TextView) findViewById(R.id.description);
        icon = (AppCompatImageView) findViewById(R.id.icon);
        // Evento del click
        selector = findViewById(R.id.selector);
        selector.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setState(state == OPEN ? CLOSED : OPEN);
            }
        });
        
        
        // Asignamos
        title.setText(attributes.getString(R.styleable.ListGroup_title));
        description.setText(attributes.getString(R.styleable.ListGroup_description));
        int iconpadding     = (int) attributes.getDimension(R.styleable.ListGroup_icon_padding, 0.0f);
        int backgroundColor = attributes.getColor(R.styleable.ListGroup_background_icon, -1);
        
        
        boolean showSelector = attributes.getBoolean(R.styleable.ListGroup_group, true);
        
        //icono
        int iconImage = (int) attributes.getResourceId(R.styleable.ListGroup_icon, -1);
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
        
        if (showSelector) {
            setState(attributes.getBoolean(R.styleable.ListGroup_open, true) ? OPEN : CLOSED);
            
        }else {
            selector.setVisibility(GONE);
            setState(OPEN);
            
        }
        
        
        attributes.recycle();
        
    }
    //endregion
    
    
    void init() {
        //asiganmos el listener del option View
        mOptionViewListener = new CheckStateTracker();
        //        mPassThroughListener = new PassThroughHierarchyChangeListener();
        //        super.setOnHierarchyChangeListener(mPassThroughListener);
        
    }
    
    
    private void onSelectOption(OptionView optionView, int optionViewId) {
        if (listener != null)
            listener.OnSelect(this, optionView, optionViewId);
    }
    
    @Override
    public void addView(View child, ViewGroup.LayoutParams params) {
        
        if (container != null && child instanceof OptionView) {
            int id = child.getId();
            if (id == View.NO_ID) {
                id = generateViewId();
                child.setId(id);
            }
            
            OptionView item = ((OptionView) child);
            item.setListener(mOptionViewListener);
            container.addView(child, params);
        } else {
            super.addView(child, params);
        }
    }
    
    @Override
    public void removeView(View view) {
        ((OptionView) view).setListener(null);
        container.removeView(view);
    }
    
    // variable de concurrencia segura
    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);
    
    /***
     * Retorna un Id sin provocar colisiones
     * @return
     */
    
    public static int generateViewId() {
        for (; ; ) {
            final int result = sNextGeneratedId.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }
    
    
    private class CheckStateTracker implements OptionView.OnClickLister {
        @Override
        public void onClick(OptionView option, int selectId) {
            onSelectOption(option, selectId);
        }
    }
    
}
