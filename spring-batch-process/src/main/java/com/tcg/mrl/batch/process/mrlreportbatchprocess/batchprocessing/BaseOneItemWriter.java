package com.tcg.mrl.batch.process.mrlreportbatchprocess.batchprocessing;

import com.tcg.mrl.batch.process.mrlreportbatchprocess.model.BaseOne;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.batch.item.ItemWriter;
import static java.util.Arrays.asList;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class BaseOneItemWriter implements ItemWriter<BaseOne> {

    private static String[] headers = {"GL_ID", "COMP_ID", "CC_ID", "SBU_ID", "LOC_ID", "MERCH_ID", "ICP_ID", "FUR_ID", "MTH_ID", "YR_ID", "DETAIL_GL", "AC_TYPE", "HEAD","MAIN_MAP",
            "DESCRIPTION", "LOC_NM", "LOC_FMT", "LOC_REMS", "LOC_DC", "CURR", "OPE_BAL", "PRD_ACTY", "CLS_BAL", "FTM_AMT", "FTM_QTD", "FTM_HYTD", "FTM_YTD", "LST_INST_UPD_DT", "REPORT_TYPE"};

//    int counter = 0;
    AtomicInteger counter = new AtomicInteger(0);

    private final Sheet sheet;

    BaseOneItemWriter(Sheet sheet) {
        this.sheet = sheet;
    }

    @Override
    public void write(List<? extends BaseOne> list) throws Exception {
        System.out.println("chunk records flushed excel");
        for (int i = 0; i < list.size(); i++) {
            if(counter.get()==0){
                createHeader();
            }else{
                writeRow(counter.get(), list.get(i));
            }
//            counter++;
            counter.getAndIncrement();
        }
    }

    private void createHeader() {
        Row header = sheet.createRow(0);
        for (int i=0 ; i<headers.length; i++){
            header.createCell(i).setCellValue(headers[i]);
        }
    }

    private void writeRow(int currentRowNumber, BaseOne book) {
        List<?> columns = prepareColumns(book);
        Row row = this.sheet.createRow(currentRowNumber);
        for (int i = 0; i < columns.size(); i++) {
            writeCell(row, i, String.valueOf(columns.get(i)));
        }
    }

    private void writeCell(Row row, int currentColumnNumber, String value) {
        Cell cell = row.createCell(currentColumnNumber);
        cell.setCellValue(value);
    }

    private List<?> prepareColumns(BaseOne baseOne) {
        return asList(
                baseOne.getBaseOneId().getGlId(),
                baseOne.getBaseOneId().getCompId(),
                baseOne.getBaseOneId().getCcId(),
                baseOne.getBaseOneId().getSbuId(),
                baseOne.getBaseOneId().getLocId(),
                baseOne.getBaseOneId().getMerchId(),
                baseOne.getBaseOneId().getIcpId(),
                baseOne.getBaseOneId().getFurId(),
                baseOne.getBaseOneId().getMthId(),
                baseOne.getBaseOneId().getYrId(),
                baseOne.getBaseOneId().getDetailGl(),
                baseOne.getAcType(),
                baseOne.getHead(),
                baseOne.getMainMap(),
                baseOne.getDescription(),
                baseOne.getLocNm(),
                baseOne.getLocFmt(),
                baseOne.getLocRems(),
                baseOne.getLocDc(),
                baseOne.getCurr(),
                baseOne.getOpeBal(),
                baseOne.getPrdActy(),
                baseOne.getClsBal(),
                baseOne.getFtmAmt(),
                baseOne.getFtmQtd(),
                baseOne.getFtmHytd(),
                baseOne.getFtmYtd(),
                baseOne.getLastUpdDt(),
                baseOne.getReportType()
        );
    }
}

