package ua.knure.fb2reader.Views.Fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ua.knure.fb2reader.DataAccess.DataAccess;
import ua.knure.fb2reader.R;
import ua.knure.fb2reader.Views.Params;

/*фрагмент для отображения списка книг в папке приложения
* */
public class BookShelfFragment extends Fragment {

    private ArrayAdapter adapter;
    private List<String> fileNames;/* Здесь будут хранится имена файлов для отображения*/
    private List<String> filePathes;/* Здесь будут хранится полные пути соответствующие именам файлов*/
    private OnBookSelectedListener onBookSelectedListener;

    /*
    * Конструктор должен быть обязательно пустой для правильной
    * инициализации фрагмента при каждом его создании
    * */
    public BookShelfFragment() {
    }

    /* Метод-фабрика который будет создавать фрагмент
     * */
    public static BookShelfFragment newInstance() {
        ArrayList<String> mfiles = new ArrayList<>();
        ArrayList<String> mpathes = new ArrayList<>();
        mfiles.clear();
        mpathes.clear();
        for (File file : DataAccess.getAllFilesInBooksFolder(DataAccess.STANDART_BOOK_FOLDER_DIRECTORY)) {
            mfiles.add(file.getName().substring(0, file.getName().lastIndexOf('.')));
            mpathes.add(file.getAbsolutePath());
        }
        BookShelfFragment fragment = new BookShelfFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(Params.ARGUMENT_ALL_NAMES_OF_BOOKS, mfiles);
        args.putStringArrayList(Params.ARGUMENT_ALL_PATHES_TO_BOOKS, mpathes);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.book_shelf_fragment, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ListView listView = (ListView) view.findViewById(R.id.book_listView);
        Bundle args = getArguments();
        fileNames = args.getStringArrayList(Params.ARGUMENT_ALL_NAMES_OF_BOOKS);
        filePathes = args.getStringArrayList(Params.ARGUMENT_ALL_PATHES_TO_BOOKS);
        adapter = new ArrayAdapter<>(getView().getContext(), R.layout.book_list_layout, fileNames);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = filePathes.get((int) id);
                onBookSelectedListener.OnBookSelectedEvent(item);
            }
        });
        getActivity().getActionBar().setTitle(Params.MENU_TITLES[Params.MENU_ID_BOOK_SHELF]);
    }

    /*
    * данный метод нужен для того что бы привязать главное активити (создание слушателя)
    * для последующей обработки события в главном активити
    * */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            onBookSelectedListener = (OnBookSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement onSomeEventListener");
        }
    }

    public interface OnBookSelectedListener {
        public void OnBookSelectedEvent(String bookPath);
    }
}