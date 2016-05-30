import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Levsha on 25.04.2016.
 */
public class Task {
    private String contractTag;
    private String contractFullName;
    private String position;
    private String workName;
    private Date beginDate;
    private Date endDate;
    private String documentName;
    private Date documentDate;
    private String department;
    private String leadingEngineer;
    private String productionEngineer;
    private String extraInfo;

    public Task(String contractTag, String contractFullName, String position, String workName, Date beginDate,
                Date endDate, String documentName, Date documentDate, String department, String leadingEngineer,
                String productionEngineer, String extraInfo) {
        setContractTag(contractTag);
        setContractFullName(contractFullName);
        setPosition(position);
        setWorkName(workName);
        setBeginDate(beginDate);
        setEndDate(endDate);
        setDocumentName(documentName);
        setDocumentDate(documentDate);
        setDepartment(department);
        setLeadingEngineer(leadingEngineer);
        setProductionEngineer(productionEngineer);
        setExtraInfo(extraInfo);
    }


    public String getContractTag() {
        return contractTag;
    }

    public void setContractTag(String contractTag) {
        this.contractTag = contractTag;
    }

    public String getContractFullName() {
        return contractFullName;
    }

    public void setContractFullName(String contractFullName) {
        this.contractFullName = contractFullName;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getWorkName() {
        return workName;
    }

    public void setWorkName(String workName) {
        this.workName = workName;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public Date getDocumentDate() {
        return documentDate;
    }

    public void setDocumentDate(Date documentDate) {
        this.documentDate = documentDate;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getLeadingEngineer() {
        return leadingEngineer;
    }

    public void setLeadingEngineer(String leadingEngineer) {
        this.leadingEngineer = leadingEngineer;
    }

    public String getProductionEngineer() {
        return productionEngineer;
    }

    public void setProductionEngineer(String productionEngineer) {
        this.productionEngineer = productionEngineer;
    }

    public String getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(String extraInfo) {
        this.extraInfo = extraInfo;
    }

    public List<String> getInfoList () {
        List<String> infoList = new ArrayList<>();
        infoList.add(getContractTag());
        infoList.add(getContractFullName());
        infoList.add(getPosition());
        infoList.add(getWorkName());
        infoList.add(dateToStringFormatted(getBeginDate()));
        infoList.add(dateToStringFormatted(getEndDate()));
        infoList.add(getDocumentName());
        infoList.add(dateToStringFormatted(getDocumentDate()));
        infoList.add(getDepartment());
        infoList.add(getLeadingEngineer());
        infoList.add(getProductionEngineer());
        infoList.add(getExtraInfo());
        return infoList;
    }

    private String dateToStringFormatted(Date date) {
        if (date == null) return "";
        StringBuilder s = new StringBuilder();
        s.append(date.getDate()).append(".").append(date.getMonth()+1).append(".").append(date.getYear()+1900);
        return s.toString();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Task{");
        sb.append("contractTag='").append(contractTag).append('\'');
        sb.append(", contractFullName='").append(contractFullName).append('\'');
        sb.append(", position='").append(position).append('\'');
        sb.append(", workName='").append(workName).append('\'');
        sb.append(", beginDate=").append(beginDate);
        sb.append(", endDate=").append(endDate);
        sb.append(", documentName='").append(documentName).append('\'');
        sb.append(", documentDate=").append(documentDate);
        sb.append(", department='").append(department).append('\'');
        sb.append(", leadingEngineer='").append(leadingEngineer).append('\'');
        sb.append(", productionEngineer='").append(productionEngineer).append('\'');
        sb.append(", extraInfo='").append(extraInfo).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
