package ua.knure.fb2reader.Views.Fragments;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Iterator;

import ua.knure.fb2reader.Book.Book;
import ua.knure.fb2reader.R;
import ua.knure.fb2reader.Views.Params;

/*
* Фрагмент для отображение инфы о книге
*
* */
public class BookInfoFragment extends Fragment {
    private Book book;
    private OnBackToReadingListener onBackToReadingListener;

    /*
    * Конструктор должен быть обязательно пустой для правильной
    * инициализации фрагмента при каждом его создании
    * */
    public BookInfoFragment() {
    }

    /* Метод-фабрика который будет создавать фрагмент с переданными в него
     * параметрами (в данном случае мы кладем туда книжку)
     * */
    public static BookInfoFragment newInstance(Book book) {
        BookInfoFragment fragment = new BookInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Params.ARGUMENT_SERIALIZED_BOOK, book);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.book_info_fragment, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
    /*
    * Грубо говоря здесь все заполнения нашего фрагмента информацией из переданной книги
    * */
        TextView myTextView = (TextView) view.findViewById(R.id.textView_book_info);
        final View mView = view;

        Bundle args = getArguments();
        book = (Book) args.getSerializable(Params.ARGUMENT_SERIALIZED_BOOK);

        if (book != null && myTextView != null) {
            myTextView.setMovementMethod(new ScrollingMovementMethod());

            Bitmap bitmapDrawable = book.getBookCover(); /* обложка книги */
            ImageSpan is = new ImageSpan(view.getContext(), bitmapDrawable);
            SpannableString ss = new SpannableString("        ");
            ss.setSpan(is, 1, 2, 0);
            myTextView.append(ss);

            StringBuilder temp = new StringBuilder();
            Iterator<String> iterator = book.getBookInfo().getBookName().iterator();

            temp.append("\nBook name :\n");
            temp.append(getInfoFromIterator(iterator));
            myTextView.append(temp.toString());

            temp = new StringBuilder();
            temp.append("\nBook author(s):\n");
            iterator = book.getBookInfo().getAuthors().iterator();
            temp.append(getInfoFromIterator(iterator));
            myTextView.append(temp.toString());

            temp = new StringBuilder();
            temp.append("\nBook publisher(s):\n");
            iterator = book.getBookInfo().getPublishInfo().iterator();
            temp.append(getInfoFromIterator(iterator));
            myTextView.append(temp.toString());

            temp = new StringBuilder();
            temp.append("\nBook genre(s):\n");
            iterator = book.getBookInfo().getGenre().iterator();
            temp.append(getInfoFromIterator(iterator));
            myTextView.append(temp.toString());

            temp = new StringBuilder();
            temp.append("\nBook translator(s):\n");
            iterator = book.getBookInfo().getTranslator().iterator();
            temp.append(getInfoFromIterator(iterator));
            myTextView.append(temp.toString());

            temp = new StringBuilder();
            temp.append("\nAnnotation:\n");
            iterator = book.getBookInfo().getAnnotation().iterator();
            temp.append(getInfoFromIterator(iterator));
            myTextView.append(temp.toString());
        }

        Button readButton = (Button) view.findViewById(R.id.buttonRead);
        readButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackToReadingListener.OnBackToReadingEvent(book);
                /*передача управления главному активити
                открытие окна для чтения книги*/
            }
        });
        getActivity().getActionBar().setTitle(Params.MENU_TITLES[Params.MENU_ID_BOOK_INFO]);
    }

    /*
    * Метод возвращает строчку с текстом полученную из нескольких строк связанных
    * с данным итератором
    * */
    private String getInfoFromIterator(Iterator<String> iterator) {
        StringBuilder builder = new StringBuilder();
        builder.append("");
        while (iterator.hasNext()) {
            builder.append(iterator.next());
        }
        String[] tempData = builder.toString().split("\n");
        builder = new StringBuilder();
        for (int i = 0; i < tempData.length; i++) {
            String[] line = tempData[i].split(" ");
            for (int j = 0; j < line.length; j++) {
                builder.append(line[j] + " ");
            }
            builder.append("\n");
        }
        if (builder.length() < 1) {
            builder.append("none");
        }
        return builder.toString();
    }

    /*
    * данный метод нужен для того что бы привязать главное активити (создание слушателя)
    * для последующей обработки события в главном активити
    * */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            onBackToReadingListener = (OnBackToReadingListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement onSomeEventListener");
        }
    }

    /*
    * интерфейс для того что бы вернуться на фрагмент чтения книги
    * */
    public interface OnBackToReadingListener {
        public void OnBackToReadingEvent(Book book);
    }
}