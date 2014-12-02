package ua.knure.fb2reader.Views;

/**
 * Created by Александр on 28.11.2014.
 */

/**
 * В этом классе содержаться константы которые служат параметрами во многих активити
 * для помещения в Bundle каки-либо аргументов, и что бы в коде не задавать их вручную
 * они были выделены в этот класс.
 * <p/>
 * Так же здесь есть константы которые отвечают за номера пунктов в боковом меню,
 * поэтому если в боковом меню поменяется расположение или элементы, то нужно будет в основном
 * редактировать только данный класс
 * <p/>
 * И еще, здесь содердится статический массив заголовков активити, который при инициализации
 * активити заполняется, а потом используется во всех участках кода в таком виде
 * Params.MENU_TITLES[Params.MENU_ID_#somthing#]. Возможно громоздко, но мне это показалось
 * достаточно удобным вариантом.
 */
public class Params {
    public static final String ARGUMENT_PAGE_NUMBER = "arg_page_number";
    public static final String ARGUMENT_PAGE_TEXT = "arg_page_text";
    public static final String ARGUMENT_TEXT_SIZE = "arg_text_size";
    public static final String ARGUMENT_TEXT_COLOR = "arg_text_color";
    public static final String ARGUMENT_BACKGROUND_COLOR = "arg_background_color";
    public static final String ARGUMENT_BOOK_PATH = "arg_book_path";
    public static final String ARGUMENT_FIRST_TIME_BOOK_INFO_OPENING = "arg_first_time_book_info_opening";
    public static final String ARGUMENT_SERIALIZED_BOOK = "arg_serialized_book";
    public static final String ARGUMENT_ALL_PATHES_TO_BOOKS = "arg_pathes_to_books";
    public static final String ARGUMENT_ALL_NAMES_OF_BOOKS = "arg_names_of_books";
    public static final int MENU_ID_BOOK_SHELF = 0;
    public static final int MENU_ID_BOOK_INFO = 1;
    public static final int MENU_ID_BOOK_ADD_BOOKMARK = 2;
    public static final int MENU_ID_BOOK_SEARCH = 3;
    public static final int MENU_ID_APP_STATISTICS = 4;
    public static final int MENU_ID_APP_SETTINGS = 5;
    public static final int MENU_ID_APP_LOGIN = 6;
    public static String[] MENU_TITLES = null;


}
