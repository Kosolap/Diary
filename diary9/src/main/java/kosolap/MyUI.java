package kosolap;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import org.hibernate.Session;

import javax.servlet.annotation.WebServlet;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 *
 */
@Theme("mytheme")
@Widgetset("kosolap.MyAppWidgetset")
public class MyUI extends UI {




//Взято из премера:

    private Table contactList = new Table(){

        @Override
        protected String formatPropertyValue(Object rowId, Object colId,
        Property property) {
            Object v = property.getValue();
            if (v instanceof Date) {
                Date dateValue = (Date) v;
                return new SimpleDateFormat("dd.MM.yy").format(dateValue);
            }
            return super.formatPropertyValue(rowId, colId, property);
        }

    };
    private TextField searchField = new TextField();
    private Button addNewContactButton = new Button("Создать новую запись");
    private Button addNewBusinessButton = new Button("Внести запись");
    private Button updateBusinessButton = new Button("Изминеть запись");
    private Button removeContactButton = new Button("Удалить запись");
    private FormLayout editorLayout = new FormLayout();
    private FieldGroup editorFields = new FieldGroup();

    //Мои поля:
    private HorizontalLayout forDidIt = new HorizontalLayout();
    private VerticalLayout rightSide = new VerticalLayout();
    private FormLayout addNewBusinessLayout = new FormLayout();
    private FieldGroup addNewBusinessFields = new FieldGroup();
    private Label forEditError = new Label();
    private Label forCreatError = new Label();

    private TextField forDate = new TextField();
    private TextField [] fieldsMass = new TextField[4];
    private CheckBox checkBox = new CheckBox();
    private CheckBox didItSearch = new CheckBox("Показывать сделанные");
    private CheckBox willDoItSearch = new CheckBox("Показывать не сделанные");


    IndexedContainer contactContainer = createDummyDatasource();

    private static final String FNAME = "Дело";
    private static final String LNAME = "Категория";
    private static final String COMPANY = "Что надо сделать";
    private static final String[] fieldNames = new String[] { FNAME, LNAME,
            COMPANY, "Выполнено", "Дата", "id"};

    private static TextField addName;
    private static TextField addCategory;
    private static TextField addWTD;
    private static TextField addDate;

    private Filter didItContactFilter = new didItContactFilter("true");
    private Filter willDoItContactFilter = new willDoItContactFilter("true");
    private Filter contactFilter = new ContactFilter("");

    private static Integer busId;

    @Override
    protected void init(VaadinRequest vaadinRequest) {

        initLayout();
        initAdder();
        initContactList();
        initEditor();
        initSearch();
        initAddRemoveButtons();

    }

    private void initLayout() {

        HorizontalSplitPanel splitPanel = new HorizontalSplitPanel();
        setContent(splitPanel);

        splitPanel.setSplitPosition(70);

        VerticalLayout leftLayout = new VerticalLayout();


        splitPanel.addComponent(leftLayout);
        splitPanel.addComponent(rightSide);

        rightSide.addComponent(editorLayout);
        rightSide.addComponent(addNewBusinessLayout);


        leftLayout.addComponent(contactList);

        forDidIt.setSpacing(true);
        forDidIt.addComponent(didItSearch);
        didItSearch.setValue(true);

        forDidIt.addComponent(willDoItSearch);
        willDoItSearch.setValue(true);

        leftLayout.addComponent(forDidIt);

        HorizontalLayout bottomLeftLayout = new HorizontalLayout();
        leftLayout.addComponent(bottomLeftLayout);
        bottomLeftLayout.addComponent(searchField);
        bottomLeftLayout.addComponent(addNewContactButton);

        leftLayout.setSizeFull();

        leftLayout.setExpandRatio(contactList, 1);
        contactList.setSizeFull();

        bottomLeftLayout.setWidth("100%");
        searchField.setWidth("100%");
        bottomLeftLayout.setExpandRatio(searchField, 1);

        editorLayout.setMargin(true);
        editorLayout.setVisible(false);

        addNewBusinessLayout.setMargin(true);
        addNewBusinessLayout.setVisible(false);
    }

    private void initEditor() {

        int i = 0;

        for (String fieldName : fieldNames) {
            if(fieldName.equals("Выполнено")){
                checkBox = new CheckBox(fieldName);
                editorLayout.addComponent(checkBox);
                checkBox.setWidth("100%");
                editorFields.bind(checkBox, fieldName);

            }

            else if(fieldName.equals("id"))
            {}

            else if(fieldName.equals("Дата"))
            {
                fieldsMass[i] = new TextField(fieldName);
                editorLayout.addComponent(fieldsMass[i]);
                fieldsMass[i].setWidth("100%");

                fieldsMass[i].setVisible(false);
                editorLayout.addComponent(forDate);
                forDate.setWidth("100%");

                editorFields.bind(fieldsMass[i], fieldName);

                i++;
            }

            else {
                fieldsMass[i] = new TextField(fieldName);
                editorLayout.addComponent(fieldsMass[i]);
                fieldsMass[i].setWidth("100%");

                editorFields.bind(fieldsMass[i], fieldName);

                i++;
            }
        }
        editorLayout.addComponent(updateBusinessButton);
        editorLayout.addComponent(removeContactButton);
        editorLayout.addComponent(forEditError);
        forEditError.setVisible(false);


    }

    private void initAdder(){

        addNewBusinessLayout.addComponent(new Label("Введите данные:"));

        addName = new TextField(FNAME);
        addNewBusinessLayout.addComponent(addName);
        addNewBusinessFields.bind(addName, FNAME);

        addCategory = new TextField(LNAME);
        addNewBusinessLayout.addComponent(addCategory);
        addNewBusinessFields.bind(addCategory, LNAME);

        addWTD = new TextField(COMPANY);
        addNewBusinessLayout.addComponent(addWTD);
        addNewBusinessFields.bind(addWTD, COMPANY);

        addDate = new TextField("Дата");
        addNewBusinessLayout.addComponent(addDate);
        addNewBusinessFields.bind(addDate, "Дата");

        addNewBusinessLayout.addComponent(addNewBusinessButton);

        addNewBusinessLayout.addComponent(forCreatError);
        forCreatError.setVisible(false);

        addNewBusinessFields.setBuffered(false);

    }

    private void initSearch() {
        searchField.setInputPrompt("Искать запись");

        searchField.setTextChangeEventMode(AbstractTextField.TextChangeEventMode.LAZY);

        searchField.addTextChangeListener(new FieldEvents.TextChangeListener() {
            public void textChange(final FieldEvents.TextChangeEvent event) {

                contactContainer.removeContainerFilter(contactFilter);
                contactFilter = new ContactFilter(event.getText());
                contactContainer.addContainerFilter(contactFilter);
            }
        });

        didItSearch.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {

                if(didItSearch.getValue().toString().equals("true"))
                {
                    contactContainer.removeContainerFilter(didItContactFilter);
                }
                else{
                contactContainer.removeContainerFilter(didItContactFilter);
                didItContactFilter = new didItContactFilter(didItSearch.getValue().toString());
                contactContainer.addContainerFilter(didItContactFilter);}
            }
        });

        willDoItSearch.addValueChangeListener(new Property.ValueChangeListener(){
            @Override
            public void valueChange(Property.ValueChangeEvent event) {

                if(willDoItSearch.getValue().toString().equals("true"))
                {
                    contactContainer.removeContainerFilter(willDoItContactFilter);
                }

                else{
                contactContainer.removeContainerFilter(willDoItContactFilter);
                willDoItContactFilter = new willDoItContactFilter(willDoItSearch.getValue().toString());
                contactContainer.addContainerFilter(willDoItContactFilter);}
            }
        });
    }

    private class willDoItContactFilter implements Filter{
        private String property;

        public willDoItContactFilter(String property)
        {
            this.property = property;
        }

        @Override
        public boolean passesFilter(Object itemId, Item item) throws UnsupportedOperationException {
            String haystack = ("" + item.getItemProperty("Выполнено").getValue());

            if(property.equals("false"))
            {
                return haystack.contains("true");
            }
            else return false;
        }

        @Override
        public boolean appliesToProperty(Object propertyId) {
            return true;
        }
    }

    private class didItContactFilter implements Filter{
        private String property;

        public didItContactFilter(String property)
        {
            this.property = property;
        }

        @Override
        public boolean passesFilter(Object itemId, Item item) throws UnsupportedOperationException {
            String haystack = ("" + item.getItemProperty("Выполнено").getValue());

            if(property.equals("false"))
            {
                return haystack.contains("false");
            }
            else return false;
        }

        @Override
        public boolean appliesToProperty(Object propertyId) {
            return true;
        }
    }

    private class ContactFilter implements Filter {
        private String needle;

        public ContactFilter(String needle) {
            this.needle = needle.toLowerCase();
        }

        public boolean passesFilter(Object itemId, Item item) {
            String haystack = ("" + item.getItemProperty(FNAME).getValue()
                    + item.getItemProperty(LNAME).getValue() + item
                    .getItemProperty(COMPANY).getValue()).toLowerCase();
            return haystack.contains(needle);
        }

        public boolean appliesToProperty(Object id) {
            return true;
        }
    }

    private void initAddRemoveButtons() {
        addNewContactButton.addClickListener(new Button.ClickListener()
        {
            public void buttonClick(ClickEvent event)
            {contactContainer.removeAllContainerFilters();

                addNewBusinessLayout.setVisible(true);
                addDate.setValue("dd.mm.yyyy");


            }
        });

        removeContactButton.addClickListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                Object contactId = contactList.getValue();

                Session session = HibernateUtil.getSessionFactory().openSession();
                session.beginTransaction();

                Business business =(Business) session.load(Business.class, Integer.parseInt(contactList.getContainerProperty(contactList.getValue(), "id").getValue().toString()));
                session.delete(business);

                session.getTransaction().commit();

                contactList.removeItem(contactId);
            }
        });

        addNewBusinessButton.addClickListener(new Button.ClickListener()
        {
            @Override
            public void buttonClick(ClickEvent clickEvent) {
                Session session = HibernateUtil.getSessionFactory().openSession();
                session.beginTransaction();

                SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");

                Date date = new Date();
                try
                {
                    date = format.parse(addDate.getValue());
                }
                catch (java.text.ParseException e)
                {
                    forCreatError.setValue("Неправильный формат даты");
                    forCreatError.setVisible(true);
                    session.getTransaction().commit();
                    return;
                }


                Business test = new Business(addName.getValue(), addWTD.getValue(), addCategory.getValue(), date);

                session.save(test);

                session.getTransaction().commit();

                Object contactId = contactContainer.addItemAt(0);

                contactList.getContainerProperty(contactId, FNAME).setValue(addName.getValue());
                contactList.getContainerProperty(contactId, LNAME).setValue(addCategory.getValue());
                contactList.getContainerProperty(contactId, COMPANY).setValue(addWTD.getValue());
                contactList.getContainerProperty(contactId, "Дата").setValue(test.getDate());
                busId ++;

                contactList.getContainerProperty(contactId, "id").setValue(busId.toString());

                contactList.select(contactId);

                addNewBusinessLayout.setVisible(false);

            }
        });

        updateBusinessButton.addClickListener(new Button.ClickListener()  {
            @Override
            public void buttonClick(ClickEvent clickEvent) {
                Object contactId = contactList.getValue();

                Session session = HibernateUtil.getSessionFactory().openSession();
                session.beginTransaction();

                Business business =(Business) session.load(Business.class, Integer.parseInt(contactList.getContainerProperty(contactList.getValue(), "id").getValue().toString()));

                business.setName(fieldsMass[0].getValue());
                business.setCategory(fieldsMass[1].getValue());
                business.setWtd(fieldsMass[2].getValue());

                if(checkBox.getValue() == null)
                    business.setDidit(null);
                else if (checkBox.getValue()) business.setDidit(1);

                else business.setDidit(null);

                SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");

                Date date = new Date();
                try
                {
                date = format.parse(forDate.getValue());
                }
                catch (java.text.ParseException e)
                {
                    forEditError.setValue("Неправильный формат даты");
                    forEditError.setVisible(true);
                    session.getTransaction().commit();
                    return;
                }

                business.setDate(date);

                String str = fieldsMass[3].getValue();
                str = forDate.getValue() + str.substring(str.indexOf(" "));
                fieldsMass[3].setValue(str);


                session.saveOrUpdate(business);

                session.getTransaction().commit();

                editorFields.setBuffered(false);

                contactList.select(contactId);

            }
        });


    }


    private void initContactList() {
        contactList.setContainerDataSource(contactContainer);
        contactList.setVisibleColumns(new String[] {FNAME, LNAME, COMPANY, "Дата"});
        contactList.setSelectable(true);
        contactList.setImmediate(true);






   /*     contactList.addValueChangeListener(new Property.ValueChangeListener() {
            public void valueChange(Property.ValueChangeEvent event) {
                Object contactId = contactList.getValue();
                if (contactId != null) {
                    editorFields.setItemDataSource(contactList.getItem(contactId));
                }

                editorLayout.setVisible(contactId != null);

            }
        });
   */

        contactList.addItemClickListener(new ItemClickEvent.ItemClickListener() {
            @Override
            public void itemClick(ItemClickEvent itemClickEvent) {


                Object contactId = itemClickEvent.getItemId();
                if (contactId != null) {

                    editorFields.setItemDataSource(contactList.getItem(contactId));
                }

                String str = fieldsMass[3].getValue().substring(0,fieldsMass[3].getValue().indexOf(" "));

                forDate.setValue(str);

                editorFields.setBuffered(true);

                forEditError.setVisible(false);
                forCreatError.setVisible(false);


                editorLayout.setVisible(contactId != null);



            }
        });
    }

     static IndexedContainer createDummyDatasource() {
        IndexedContainer ic = new IndexedContainer();

        //Берём список дел из базы данных
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yy");

        List<Business> list = session.createQuery("from Business").list();

        session.getTransaction().commit();

         Collections.sort(list);

        //Заносим список дел в таблицу
        for (String p : fieldNames) {
            if(!p.equals("Дата"))
            ic.addContainerProperty(p, String.class, "");
            else
            ic.addContainerProperty("Дата", Date.class,null);
        }

        busId = 0;

        for (int i = 0; i < list.size(); i++) {
            Object id = ic.addItem();
            ic.getContainerProperty(id, FNAME).setValue(list.get(i).getName());
            ic.getContainerProperty(id, LNAME).setValue(list.get(i).getCategory());
            ic.getContainerProperty(id, COMPANY).setValue(list.get(i).getWtd());
            ic.getContainerProperty(id, "Дата").setValue(list.get(i).getDate());
            ic.getContainerProperty(id, "id").setValue(list.get(i).getId().toString());


            if(list.get(i).getDidit() != null)
            {
                if(list.get(i).getDidit() !=0)
                ic.getContainerProperty(id, "Выполнено").setValue("true");

                else ic.getContainerProperty(id, "Выполнено").setValue("false");
            }
            else ic.getContainerProperty(id, "Выполнено").setValue("false");

            if(list.get(i).getId()>busId) busId = list.get(i).getId();
        }

        return ic;
    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }


}
