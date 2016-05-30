import exceptions.InfoException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import properties.PropertyUtils;

import java.io.*;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Stream;

/**
 * Created by Levsha on 25.04.2016.
 */
public class RP_ExcelUtils {
    public static List<Report> readFiles(List<File> files) {
        if (files == null) return null;
        List<Report> allPossibleReportsList = new ArrayList<>();
        for (File excelFile : files) {
            if (excelFile.isDirectory()) continue;
            TaskType reportType;
            List<Task> tasks = new ArrayList<>();
            int rowStartPosition = 10;
            try {
                XSSFSheet sheet = getWorkbook(excelFile).getSheetAt(0);
                //define report type
                if (excelFile.getName().contains("РД") || excelFile.getName().contains("ПСД")) {
                    reportType = TaskType.РД;
                } else if (excelFile.getName().contains("ВЗ")) {
                    reportType = TaskType.ВЗ;
                } else {
                    reportType = TaskType.Поручение;
                }
                //get tasks
                Iterator<Row> iter = sheet.rowIterator();
                while (iter.hasNext()) {
                    Row row = iter.next();
                    if (row.getRowNum() > rowStartPosition) {
                        String contractTag = getStringCellValue(row.getCell(1));
                        String contractFullName = getStringCellValue(row.getCell(2));
                        String position = getStringCellValue(row.getCell(3));
                        String workName = getStringCellValue(row.getCell(4));
                        Date beginDate = getDateCellValue(row.getCell(7));
                        Date endDate = getDateCellValue(row.getCell(8));
                        String docName = getStringCellValue(row.getCell(11));
                        Date docDate = getDateCellValue(row.getCell(10));
                        String department = getStringCellValue(row.getCell(12));
                        String leadingEngineer = getStringCellValue(row.getCell(13));
                        String productionEngineer = getStringCellValue(row.getCell(14));
                        String extraInfo = getStringCellValue(row.getCell(15));
                        tasks.add(new Task(contractTag, contractFullName, position, workName, beginDate,
                                endDate, docName, docDate, department, leadingEngineer,
                                productionEngineer, extraInfo));
                    }
                }
                allPossibleReportsList.add(new Report(reportType, tasks));
            } catch (Exception ex) {
                //ex.printStackTrace();
                throw new InfoException("Не могу прочитать ведомости.");
            }
        }
        return allPossibleReportsList;
    }

    private static XSSFWorkbook getWorkbook(File file) {
        String fileType = file.getName().substring(file.getName().lastIndexOf("."));
        XSSFWorkbook workBook;
        try {
            switch (fileType) {
                case ".xlsx":
                    workBook = new XSSFWorkbook(file);
                    break;
                default:
                    throw new IOException();
            }
        } catch (Exception ex) {
            throw new InfoException("Can't read workbook in ".concat(fileType).concat(" file ").concat(file.getName()));
        }
        return workBook;
    }

    public static void writeFiles(Set<String> engineerNames, List<Report> reports, String pathName, LocalDate start, LocalDate end, String month) {
        for (String engineer : engineerNames) {
            writeFile(reports, engineer, pathName, start, end, month);
        }
    }

    public static void writeFile(List<Report> reports, String name, String pathName, LocalDate start, LocalDate end, String month) {
        File file = new File(pathName.concat("/План на ").concat(month)
                .concat(" ").concat(name).concat(" (сформирован ")
                .concat(LocalDate.now(Clock.systemDefaultZone()).toString().replaceAll("-", "."))
                .concat(").xlsx"));
        List<Report> engineerReportList = new ArrayList<>();
        for (Report report : reports) {
            Stream<Task> taskStream = report.getTasks().stream();
            taskStream = taskStream.filter(task -> task.getLeadingEngineer().contains(name) ||
                    task.getProductionEngineer().contains(name));
            taskStream = taskStream.sorted((task1, task2) -> task1.getContractTag().compareTo(task2.getContractTag()));
            List<Task> currentTasks = new ArrayList<>();
            taskStream.forEach(task -> {
                Date taskDate = task.getEndDate();
                if (taskDate == null ||
                        (taskDate.after(Date.from(start.atStartOfDay(ZoneId.systemDefault()).toInstant())) &&
                                taskDate.before(Date.from(end.atStartOfDay(ZoneId.systemDefault()).toInstant())))) currentTasks.add(task);
            });
            engineerReportList.add(new Report(report.getStage(), currentTasks));
        }
        try {
            XSSFWorkbook workbook = getReportTemplateWorkbook();
            XSSFSheet sheet = workbook.getSheetAt(0);
            //устаноовим имя отчетуемого
            sheet.getRow(2).createCell(1).setCellValue(name);
            //заполним отчет информацией
            Row row;
            Cell cell;
            //запишем стили с темплейта
            List<CellStyle> cellStyles = new ArrayList<>();
            sheet.getRow(sheet.getLastRowNum()).cellIterator().forEachRemaining(c1 -> cellStyles.add(c1.getCellStyle()));

            CellStyle header1Style = workbook.createCellStyle();
            header1Style.cloneStyleFrom(cellStyles.get(0));
            Font font1 = workbook.createFont();
            font1.setColor(IndexedColors.DARK_RED.getIndex());
            header1Style.setFont(font1);

            CellStyle header2Style = workbook.createCellStyle();
            Font font2 = workbook.createFont();
            font2.setColor(IndexedColors.BLUE.getIndex());
            font2.setBold(true);
            font2.setUnderline((byte)1);
            font2.setFontHeightInPoints((short)12);
            header2Style.setFont(font2);

            int rowStartPointer = sheet.getLastRowNum() + 1;
            Collections.sort(engineerReportList, (o1, o2) -> o1.getStage().ordinal()-o2.getStage().ordinal());

            Map <String, List<Task>> uniqueTaskList;
            Set<String> mapKeys;
            List<String> taskInfoList;

            for (Report report : engineerReportList) {
                //идентификатор стадии ВЗ/РД/Поручение
                row = sheet.createRow(rowStartPointer++);
                row.createCell(0).setCellValue(report.getStage().getName());
                row.getCell(0).setCellStyle(header2Style);
                uniqueTaskList = report.getTaskMapByObjTag();
                mapKeys = uniqueTaskList.keySet();
                for (String key : mapKeys) {
                    //вписываем наименование объекта - заголовок
                    row = sheet.createRow(rowStartPointer++);
                    row.createCell(0).setCellValue(key);
                    row.getCell(0).setCellStyle(header1Style);
                    //заполним пустые ячейки
                    for (int i = 1; i < cellStyles.size() ; i++) {
                        row.createCell(i).setCellStyle(cellStyles.get(i));
                    }

                    for (Task task : uniqueTaskList.get(key)) {
                        taskInfoList = task.getInfoList();
                        //заполняем работы по объекту
                        row = sheet.createRow(rowStartPointer++);
                        for (int i = 2, j = 0; i < taskInfoList.size(); i++, j++) {
                            cell = row.createCell(j);
                            cell.setCellStyle(cellStyles.get(j));
                            cell.setCellValue(taskInfoList.get(i));
                            if (j == 2) {
                                j++;
                                cell = row.createCell(j);
                                cell.setCellStyle(cellStyles.get(j));
                            }
                        }
                    }
                }
            }
            try (FileOutputStream fos = new FileOutputStream(file)) {
                workbook.write(fos);
            } catch (IOException e) {
                //e.printStackTrace();
                throw new InfoException("Ошибка сохранения отчета в файл.");
            }
        } catch (Exception e) {
            //e.printStackTrace();
            throw new InfoException("Не могу создать отчеты.");
        }
    }


    private static String getStringCellValue(Cell cell) {
        String s = "";
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_BLANK:
                break;
            case Cell.CELL_TYPE_BOOLEAN:
                s += cell.getBooleanCellValue();
                break;
            case Cell.CELL_TYPE_ERROR:
                s += cell.getErrorCellValue();
                break;
            case Cell.CELL_TYPE_FORMULA:
                s += cell.getCellFormula();
                break;
            case Cell.CELL_TYPE_NUMERIC:
                s += (int)cell.getNumericCellValue();
                break;
            case Cell.CELL_TYPE_STRING:
                s += cell.getStringCellValue();
                break;
        }
        return s;
    }

    private static Date getDateCellValue(Cell cell) {
        Date date;
        try {
            date = cell.getDateCellValue();
        } catch (Exception e) {
            //e.printStackTrace();
            return null;
        }
        return date;
    }

    private static XSSFWorkbook getReportTemplateWorkbook() {
        XSSFWorkbook workBook;
        Properties properties = PropertyUtils.getProperties();
        //String name = properties.getProperty("TEMPLATE");
        String name = "ReportTemplate.xlsx";
        try (InputStream inputStream = new FileInputStream(name)){
            workBook = new XSSFWorkbook(inputStream);
        } catch (IOException e) {
            //e.printStackTrace();
            throw new InfoException("Не нахожу ReportTemplate.xlsx.");
        }
        return workBook;
    }
}