/**
 * Created by Levsha on 25.04.2016.
 */
public enum TaskType {
    РД ("Рабочая документация"),
    ВЗ ("Взаимные задания"),
    Поручение ("Поручения руководства");

    private String name;
    TaskType (String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
