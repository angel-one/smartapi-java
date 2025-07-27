package com.angelbroking.smartapi.algos;

import com.angelbroking.smartapi.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AngelOneChargeCalculator {

    private static final Logger log = LoggerFactory.getLogger(AngelOneChargeCalculator.class);

    /**
     * Calculates estimated total charges for a single trade (buy or sell leg).
     * This is a simplified calculation based on common Angel One brokerage structure.
     *
     * @param price           Execution price of the trade.
     * @param quantity        Quantity traded.
     * @param transactionType "BUY" or "SELL".
     * @param productType     e.g., "DELIVERY", "INTRADAY", "MARGIN", "BO", "CO", "FNO" (Futures/Options).
     * @param exchange        e.g., "NSE", "BSE", "NFO", "MCX".
     * @return Total estimated charges for this trade leg.
     */
    public double calculateTotalCharges(double price, int quantity, String transactionType, String productType, String exchange) {
        if (price <= 0 || quantity <= 0) {
            return 0.0; // No trade, no charges
        }

        double turnover = price * quantity;
        double brokerage = 0.0;
        double stt = 0.0;
        double transactionCharges = 0.0;
        double sebiFees = 0.0;
        double stampDuty = 0.0;
        double gst = 0.0;

        // 1. Brokerage
        switch (productType) {
            case Constants.PRODUCT_DELIVERY:
                brokerage = 0.0; // Zero brokerage for Equity Delivery
                break;
            case Constants.PRODUCT_INTRADAY:
            case Constants.PRODUCT_MARGIN: // Assuming similar brokerage for Margin
            case Constants.PRODUCT_BO: // Bracket Order
            case Constants.PRODUCT_CO: // Cover Order
                // 0.03% of turnover or Rs 20, whichever is lower
                brokerage = Math.min(0.0003 * turnover, 20.0);
                break;
            case Constants.PRODUCT_CARRYFORWARD: // Assuming F&O or similar flat rate products
            case "FNO": // Explicit F&O type if used
                 // Flat Rs 20 per executed order (leg)
                brokerage = 20.0;
                break;
            default:
                log.warn("Unknown product type for charge calculation: {}", productType);
                // Default to a conservative flat fee if product type is unknown
                brokerage = 20.0;
                break;
        }

        // 2. STT (Securities Transaction Tax)
        // Calculated on turnover, varies by product and transaction type (Buy/Sell)
        switch (productType) {
            case Constants.PRODUCT_DELIVERY:
                stt = 0.001 * turnover; // 0.1% on Buy and Sell
                break;
            case Constants.PRODUCT_INTRADAY:
            case Constants.PRODUCT_MARGIN:
            case Constants.PRODUCT_BO:
            case Constants.PRODUCT_CO:
                if ("SELL".equalsIgnoreCase(transactionType)) {
                    stt = 0.00025 * turnover; // 0.025% on Sell
                }
                break;
            case Constants.PRODUCT_CARRYFORWARD:
            case "FNO":
                if ("SELL".equalsIgnoreCase(transactionType)) {
                    // F&O STT varies by segment (Futures/Options) and value (Premium/Settlement)
                    // Using simplified rates: 0.0125% on Sell Futures, 0.05% on Sell Options Premium
                    // This calculator doesn't distinguish Futures/Options or premium/settlement,
                    // so we'll use a general F&O sell rate or require productType to be more specific (e.g., "FNO_FUT", "FNO_OPT")
                    // For simplicity, let's assume a blended rate or require specific product types.
                    // If productType is just "FNO", this is an approximation.
                    // A more accurate way needs segment info. Let's use a placeholder/average.
                    // For now, let's assume the productType might be "FNO_OPT" or "FNO_FUT" or just "FNO"
                    if ("FNO_OPT".equalsIgnoreCase(productType)) {
                         // STT on Options is on premium value on SELL side
                         // Assuming 'price' here is the premium per share/lot
                         stt = 0.0005 * turnover; // 0.05% on Sell Options Premium
                    } else { // Assuming Futures or general FNO
                         stt = 0.000125 * turnover; // 0.0125% on Sell Futures
                    }
                }
                break;
        }

        // 3. Transaction Charges (Exchange Turnover Tax)
        // Varies by exchange and segment (Equity/F&O)
        double transactionChargeRate = 0.0;
        if ("NSE".equalsIgnoreCase(exchange)) {
            switch (productType) {
                case Constants.PRODUCT_DELIVERY:
                case Constants.PRODUCT_INTRADAY:
                case Constants.PRODUCT_MARGIN:
                case Constants.PRODUCT_BO:
                case Constants.PRODUCT_CO:
                    transactionChargeRate = 0.0000345; // 0.00345% for Equity
                    break;
                case Constants.PRODUCT_CARRYFORWARD:
                case "FNO":
                case "FNO_FUT":
                    transactionChargeRate = 0.000018; // 0.0018% for Futures
                    break;
                case "FNO_OPT":
                    transactionChargeRate = 0.00053; // 0.053% for Options (on premium value)
                    break;
            }
        } else if ("BSE".equalsIgnoreCase(exchange)) {
             switch (productType) {
                case Constants.PRODUCT_DELIVERY:
                case Constants.PRODUCT_INTRADAY:
                case Constants.PRODUCT_MARGIN:
                case Constants.PRODUCT_BO:
                case Constants.PRODUCT_CO:
                    // BSE has different slabs, using an average/common rate
                    transactionChargeRate = 0.0000345; // Approximation for Equity
                    break;
                 // Add BSE F&O rates if needed
            }
        }
        // Add MCX, CDS if needed

        transactionCharges = transactionChargeRate * turnover;

        // 4. GST (Goods and Services Tax)
        // 18% on Brokerage + Transaction Charges
        gst = 0.18 * (brokerage + transactionCharges);

        // 5. SEBI Turnover Fees
        // 0.0001% on turnover
        sebiFees = 0.000001 * turnover;

        // 6. Stamp Duty
        // Applied only on BUY side, varies by state and segment. Using common rates.
        if ("BUY".equalsIgnoreCase(transactionType)) {
             switch (productType) {
                case Constants.PRODUCT_DELIVERY:
                    stampDuty = 0.00015 * turnover; // 0.015%
                    break;
                case Constants.PRODUCT_INTRADAY:
                case Constants.PRODUCT_MARGIN:
                case Constants.PRODUCT_BO:
                case Constants.PRODUCT_CO:
                    stampDuty = 0.00003 * turnover; // 0.003%
                    break;
                 case Constants.PRODUCT_CARRYFORWARD:
                 case "FNO":
                 case "FNO_FUT":
                    stampDuty = 0.00002 * turnover; // 0.002%
                    break;
                 case "FNO_OPT":
                    stampDuty = 0.00003 * turnover; // 0.003%
                    break;
             }
        }

        double totalCharges = brokerage + stt + transactionCharges + gst + sebiFees + stampDuty;

        // Log the breakdown for debugging
        log.debug("Charges for {} {} {} shares @ {:.2f} (Turnover: {:.2f}): Brokerage={:.4f}, STT={:.4f}, TxnCharges={:.4f}, GST={:.4f}, SEBI={:.4f}, StampDuty={:.4f} | Total={:.4f}",
                transactionType, productType, quantity, price, turnover, brokerage, stt, transactionCharges, gst, sebiFees, stampDuty, totalCharges);

        return totalCharges;
    }
}