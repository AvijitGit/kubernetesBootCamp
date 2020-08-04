package com.tcg.mrl.batch.process.mrlreportbatchprocess.batchprocessing;

import com.tcg.mrl.batch.process.mrlreportbatchprocess.model.BaseThree;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.springframework.batch.item.ItemWriter;

import java.util.List;

import static java.util.Arrays.asList;

public class BaseThreeItemWriter implements ItemWriter<BaseThree> {

    private static String[] headers = { "STORE_CODE_OFIN", "RETEK_CODE", "LOC_FMT", "MTH_ID", "YR_ID", "DIVISION", "MNTH", "STORE_NM", "CITY", "DC", "ZONE", "REG", "BUS_TYPE", "STAT",
            "PRJT_GOLD_STATUS", "STORE_LAUNCH_DATE", /*"STORE_CLOSURE_DATE",*/ "STORE_AREA", "DIV_AREA", "RETAIL_AREA", "SQ_FEET_DAYS", "RETAIL_SQFT_DAYS", "NO_OF_TICKETS", "ATV", "RLSD_SALES", "GST", "NET_SALES",
            "COST_OF_PURCHASE", "OCTROI", "INBOUND_TRPTN" ,"RDC", "PRCSSNG_CNTR_COST", "COST_OF_GOODS_SOLD", "PROMO_RCVRY_FRM_VNDRS", "DUMP", "LOSS_ON_EXPIRY_NRTV", "GROSS_MARGIN", "OFF_INVOICE",
            "LSTNG_FEES", "DISPLAY_VISIBILITY_FEES", "OTHERS","MERCHANDISING_INCOME","MARKETING_INCOME","MISCELLANEOUS_INCOME","TOTAL_OTHER_INCOME","DORMANCY_NRV" ,"COMMERCIAL_INCM","MANPOWER","SHRINKAGE","DAMAGES","FREIGHT_OUT_WAREHOUSE", "RENT","BROKERAGE_COST","HOUSEKEEPING_CHARGES","SECURITY_COST","UTILITIES","CONSUMABLES","REPAIR_MAINTAINENCE",
            "FINANCE_CHARGES","PRINTING_STATIONERY","TRAVEL_CONVEYANCE","RATES_TAXES","MISC_EXPENSES","COMMUNICATION","IT_OPEX","WRITEOFFS","SHUTTLE_OPERATING_EXPENSES","INSURANCE_EXPENSES","STORE_OVERHEADS","STORE_CONTRIBUTION","FV_STATUS","TRPT_COST_SCM","TRPT_COST_FV","TRPT_COST_FV","OTHR_SCM_COST","TTL_COST_SCM","DIST_FRM_DC","DIRECT_MRKTG_COST"
            ,"ZONE_MRKTG_COST","CORP_MRKTG_COST","PL_MRKTG_COST","MRKTNG_COST","STORE_EBITDA","VRBLE_EBITDA","CIRCLE_OVERHEAD","CIRCLE_EBITDA","CORP_OVERHEAD","FORMAT_OVERHEAD","STORE_CLOSURE_COST","EXCPTNL_NON_RECRR_ITMS","PRE_OPRTNG_EXPNSES" ,"DEPRECIATION","LEASE_FINANCE","INTEREST","RUNTIME"
    };


    int counter = 0;

    private final Sheet sheet;

    public BaseThreeItemWriter(SXSSFSheet sheet) {
        this.sheet = sheet;
    }

    @Override
    public void write(List<? extends BaseThree> list) throws Exception {
        System.out.println("chunk3 records flushed excel3");
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
    private void writeRow(int currentRowNumber, BaseThree baseThree) {
        List<?> columns = prepareColumns(baseThree);
        Row row = this.sheet.createRow(currentRowNumber);
        for (int i = 0; i < columns.size(); i++) {
            writeCell(row, i, String.valueOf(columns.get(i)));
        }
    }

    private void writeCell(Row row, int currentColumnNumber, String value) {
        Cell cell = row.createCell(currentColumnNumber);
        cell.setCellValue(value);
    }

    private List<?> prepareColumns(BaseThree baseThree) {
        return asList(
                baseThree.getBaseThreeId().getStoreCodeOfin(),
                baseThree.getBaseThreeId().getRetekCode(),
                baseThree.getBaseThreeId().getLocFmt(),
                baseThree.getBaseThreeId().getMthId(),
                baseThree.getBaseThreeId().getYrId(),
                baseThree.getBaseThreeId().getDivision(),
                baseThree.getMnth(),
                baseThree.getStoreNm(),
                baseThree.getCity(),
                baseThree.getDc(),
                baseThree.getZone(),
                baseThree.getReg(),
                baseThree.getBusType(),
                baseThree.getStart(),
                baseThree.getPrjtGoldStatus(),
                baseThree.getStoreLaunchDate(),
//                baseThree.getStoreClosureDate(),
                baseThree.getStoreArea(),
                baseThree.getDivArea(),
                baseThree.getRetailArea(),
                baseThree.getSqFeetDays(),
                baseThree.getRetailSqftDays(),
                baseThree.getNoOfTickets(),
                baseThree.getAtv(),
                baseThree.getRlsdSales(),
                baseThree.getGst(),
                baseThree.getNetSales(),
                baseThree.getCostOfPurchase(),
                baseThree.getOctroi(),
                baseThree.getInboundTrptn(),
                baseThree.getRdc(),
                baseThree.getPrcssngCntrCost(),
                baseThree.getCostOfGoodsSold(),
                baseThree.getPromoRcvryFrmVndrs(),
                baseThree.getDump(),
                baseThree.getLossOnExpiryNrtv(),
                baseThree.getGrossMargin(),
                baseThree.getOffInvoice(),
                baseThree.getLstngFees(),
                baseThree.getDisplayVisibilityFees(),
                baseThree.getOthers(),
                baseThree.getMerchandisingIncome(),
                baseThree.getMarketingIncome(),
                baseThree.getMiscellaneousIncome(),
                baseThree.getTotalOtherIncome(),
                baseThree.getDormancyNrv(),
                baseThree.getCommercialIncm(),
                baseThree.getManpower(),
                baseThree.getShrinkage(),
                baseThree.getDamages(),
                baseThree.getFreightOutWarehouse(),
                baseThree.getRent(),
                baseThree.getBrokerageCost(),
                baseThree.getHousekeepingCharges(),
                baseThree.getSecurityCost(),
                baseThree.getUtilities(),
                baseThree.getConsumables(),
                baseThree.getRepairMaintainence(),
                baseThree.getFinanceCharges(),
                baseThree.getFinanceCharges(),
                baseThree.getPrintingStationery(),
                baseThree.getTravelConveyance(),
                baseThree.getRatesTaxes(),
                baseThree.getMiscExpenses(),
                baseThree.getCommunication(),
                baseThree.getItOpex(),
                baseThree.getWriteoffs(),
                baseThree.getShuttleOperatingExpenses(),
                baseThree.getInsuranceExpenses(),
                baseThree.getStoreOverheads(),
                baseThree.getStoreContribution(),
                baseThree.getFvStatus(),
                baseThree.getTrptCostScm(),
                baseThree.getTrptCostFv(),
                baseThree.getOthrScmCost(),
                baseThree.getTtlCostScm(),
                baseThree.getDistFrmDc(),
                baseThree.getDirectMrktgCost(),
                baseThree.getZoneMrktgCost(),
                baseThree.getCorpMrktgCost(),
                baseThree.getPlMrktgCost(),
                baseThree.getMrktngCost(),
                baseThree.getStoreEbitda(),
                baseThree.getVrbleEbitda(),
                baseThree.getCircleOverhead(),
                baseThree.getCircleEbitda(),
                baseThree.getCorpOverhead(),
                baseThree.getFormatOverhead(),
                baseThree.getStoreClosureCost(),
                baseThree.getExcptnlNonRecrrItms(),
                baseThree.getPreOprtngExpnses(),
                baseThree.getDepreciation(),
                baseThree.getLeaseFinance(),
                baseThree.getInterest(),
                baseThree.getRuntime()
        );
    }
}
