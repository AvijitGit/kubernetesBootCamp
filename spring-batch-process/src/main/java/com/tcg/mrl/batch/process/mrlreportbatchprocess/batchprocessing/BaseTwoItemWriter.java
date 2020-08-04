package com.tcg.mrl.batch.process.mrlreportbatchprocess.batchprocessing;

import com.tcg.mrl.batch.process.mrlreportbatchprocess.model.BaseTwo;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.springframework.batch.item.ItemWriter;

import javax.persistence.Column;
import java.util.Date;
import java.util.List;

import static java.util.Arrays.asList;

public class BaseTwoItemWriter implements ItemWriter<BaseTwo> {

    private static String[] headers = {"LOC_ID", "MTH_ID", "YR_ID", "LOC_NM", "LOC_FMT", "LOC_REMS", "LOC_DC",  "HEAD", "AC_TYPE", "MAPPING", "AC_GRP", "AC_SUB_GRP",
            "FTM",  "QTD", "HYTD" , "YTD", "MTH_CLS_BAL", "PREV_MTH_CLS_BAL", "LST_INST_UPD_DT", "REPORT_TYPE"};


    int counter = 0;

    private final Sheet sheet;

    public BaseTwoItemWriter(SXSSFSheet sheet) {
        this.sheet = sheet;
    }

    @Override
    public void write(List<? extends BaseTwo> list) throws Exception {
        System.out.println("chunk2 records flushed excel2");
        for (int i = 0; i < list.size(); i++) {
            if(counter==0){
                createHeader();
            }else{
                writeRow(counter, list.get(i));
            }
            counter++;
        }
    }

    private void createHeader() {
        Row header = sheet.createRow(0);
        for (int i=0 ; i<headers.length; i++){
            header.createCell(i).setCellValue(headers[i]);
        }
    }
    private void writeRow(int currentRowNumber, BaseTwo baseTwo) {
        List<?> columns = prepareColumns(baseTwo);
        Row row = this.sheet.createRow(currentRowNumber);
        for (int i = 0; i < columns.size(); i++) {
            writeCell(row, i, String.valueOf(columns.get(i)));
        }
    }

    private void writeCell(Row row, int currentColumnNumber, String value) {
        Cell cell = row.createCell(currentColumnNumber);
        cell.setCellValue(value);
    }

    private List<?> prepareColumns(BaseTwo baseTwo) {
        return asList(
                baseTwo.getBaseTwoId().getLocId(),
                baseTwo.getBaseTwoId().getMthId(),
                baseTwo.getBaseTwoId().getYrId(),
                baseTwo.getBaseTwoId().getLocNm(),
                baseTwo.getBaseTwoId().getLocFmt(),
                baseTwo.getBaseTwoId().getLocRems(),
                baseTwo.getBaseTwoId().getLocDc(),
                baseTwo.getBaseTwoId().getHead(),
                baseTwo.getBaseTwoId().getAcType(),
                baseTwo.getBaseTwoId().getMapping(),
                baseTwo.getBaseTwoId().getAcGrp(),
                baseTwo.getBaseTwoId().getAcSubGrp(),
                baseTwo.getFtm(),
                baseTwo.getQtd(),
                baseTwo.getHytd(),
                baseTwo.getYtd(),
                baseTwo.getMthClsBal(),
                baseTwo.getPrevMthClsBal(),
                baseTwo.getBaseTwoId().getReportType(),
                baseTwo.getLstInstUpdDt()
        );
    }

}
