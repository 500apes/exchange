/*
 * This file is part of Bisq.
 *
 * Bisq is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * Bisq is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Bisq. If not, see <http://www.gnu.org/licenses/>.
 */

package io.bisq.core.payment.payload;

import com.google.protobuf.Message;
import io.bisq.common.locale.BankUtil;
import io.bisq.common.locale.CountryUtil;
import io.bisq.common.locale.Res;
import io.bisq.generated.protobuffer.PB;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;
import java.util.Optional;

@EqualsAndHashCode(callSuper = true)
@ToString
@Setter
@Getter
@Slf4j
public class CashDepositAccountPayload extends CountryBasedPaymentAccountPayload {
    private String holderName;
    @Nullable
    private String holderEmail;
    @Nullable
    private String bankName;
    @Nullable
    private String branchId;
    @Nullable
    private String accountNr;
    @Nullable
    private String accountType;
    @Nullable
    private String requirements;
    @Nullable
    private String holderTaxId;
    @Nullable
    private String bankId;

    public CashDepositAccountPayload(String paymentMethod, String id, long maxTradePeriod) {
        super(paymentMethod, id, maxTradePeriod);
    }


    ///////////////////////////////////////////////////////////////////////////////////////////
    // PROTO BUFFER
    ///////////////////////////////////////////////////////////////////////////////////////////

    private CashDepositAccountPayload(String paymentMethodName,
                                      String id,
                                      long maxTradePeriod,
                                      String countryCode,
                                      String holderName,
                                      @SuppressWarnings("NullableProblems") String holderEmail,
                                      @SuppressWarnings("NullableProblems") String bankName,
                                      @SuppressWarnings("NullableProblems") String branchId,
                                      @SuppressWarnings("NullableProblems") String accountNr,
                                      @SuppressWarnings("NullableProblems") String accountType,
                                      @SuppressWarnings("NullableProblems") String requirements,
                                      @SuppressWarnings("NullableProblems") String holderTaxId,
                                      @SuppressWarnings("NullableProblems") String bankId) {
        super(paymentMethodName, id, maxTradePeriod, countryCode);
        this.holderName = holderName;
        this.holderEmail = holderEmail;
        this.bankName = bankName;
        this.branchId = branchId;
        this.accountNr = accountNr;
        this.accountType = accountType;
        this.requirements = requirements;
        this.holderTaxId = holderTaxId;
        this.bankId = bankId;
    }

    @Override
    public Message toProtoMessage() {
        PB.CashDepositAccountPayload.Builder builder =
                PB.CashDepositAccountPayload.newBuilder()
                        .setHolderName(holderName);
        Optional.ofNullable(holderEmail).ifPresent(builder::setHolderEmail);
        Optional.ofNullable(bankName).ifPresent(builder::setBankName);
        Optional.ofNullable(branchId).ifPresent(builder::setBranchId);
        Optional.ofNullable(accountNr).ifPresent(builder::setAccountNr);
        Optional.ofNullable(accountType).ifPresent(builder::setAccountType);
        Optional.ofNullable(requirements).ifPresent(builder::setRequirements);
        Optional.ofNullable(holderTaxId).ifPresent(builder::setHolderTaxId);
        Optional.ofNullable(bankId).ifPresent(builder::setBankId);

        final PB.CountryBasedPaymentAccountPayload.Builder countryBasedPaymentAccountPayload = getPaymentAccountPayloadBuilder()
                .getCountryBasedPaymentAccountPayloadBuilder()
                .setCashDepositAccountPayload(builder);
        return getPaymentAccountPayloadBuilder()
                .setCountryBasedPaymentAccountPayload(countryBasedPaymentAccountPayload)
                .build();
    }

    public static PaymentAccountPayload fromProto(PB.PaymentAccountPayload proto) {
        PB.CountryBasedPaymentAccountPayload countryBasedPaymentAccountPayload = proto.getCountryBasedPaymentAccountPayload();
        PB.CashDepositAccountPayload cashDepositAccountPayload = countryBasedPaymentAccountPayload.getCashDepositAccountPayload();
        return new CashDepositAccountPayload(proto.getPaymentMethodId(),
                proto.getId(),
                proto.getMaxTradePeriod(),
                countryBasedPaymentAccountPayload.getCountryCode(),
                cashDepositAccountPayload.getHolderName(),
                cashDepositAccountPayload.getHolderEmail().isEmpty() ? null : cashDepositAccountPayload.getHolderEmail(),
                cashDepositAccountPayload.getBankName().isEmpty() ? null : cashDepositAccountPayload.getBankName(),
                cashDepositAccountPayload.getBranchId().isEmpty() ? null : cashDepositAccountPayload.getBranchId(),
                cashDepositAccountPayload.getAccountNr().isEmpty() ? null : cashDepositAccountPayload.getAccountNr(),
                cashDepositAccountPayload.getAccountType().isEmpty() ? null : cashDepositAccountPayload.getAccountType(),
                cashDepositAccountPayload.getRequirements().isEmpty() ? null : cashDepositAccountPayload.getRequirements(),
                cashDepositAccountPayload.getHolderTaxId().isEmpty() ? null : cashDepositAccountPayload.getHolderTaxId(),
                cashDepositAccountPayload.getBankId().isEmpty() ? null : cashDepositAccountPayload.getBankId());
    }


    ///////////////////////////////////////////////////////////////////////////////////////////
    // API
    ///////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String getPaymentDetails() {
        return "Cash deposit - " + getPaymentDetailsForTradePopup().replace("\n", ", ");
    }

    @Override
    public String getPaymentDetailsForTradePopup() {
        String bankName = BankUtil.isBankNameRequired(countryCode) ?
                BankUtil.getBankNameLabel(countryCode) + " " + this.bankName + "\n" : "";
        String bankId = BankUtil.isBankIdRequired(countryCode) ?
                BankUtil.getBankIdLabel(countryCode) + " " + this.bankId + "\n" : "";
        String branchId = BankUtil.isBranchIdRequired(countryCode) ?
                BankUtil.getBranchIdLabel(countryCode) + " " + this.branchId + "\n" : "";
        String accountNr = BankUtil.isAccountNrRequired(countryCode) ?
                BankUtil.getAccountNrLabel(countryCode) + " " + this.accountNr + "\n" : "";
        String accountType = BankUtil.isAccountTypeRequired(countryCode) ?
                BankUtil.getAccountTypeLabel(countryCode) + " " + this.accountType + "\n" : "";
        String holderIdString = BankUtil.isHolderIdRequired(countryCode) ?
                (BankUtil.getHolderIdLabel(countryCode) + " " + holderTaxId + "\n") : "";
        String requirementsString = requirements != null && !requirements.isEmpty() ?
                ("Extra requirements: " + requirements + "\n") : "";
        String emailString = holderEmail != null ?
                (Res.get("payment.email") + " " + holderEmail + "\n") : "";

        return "Holder name: " + holderName + "\n" +
                emailString +
                bankName +
                bankId +
                branchId +
                accountNr +
                accountType +
                holderIdString +
                requirementsString +
                "Country of bank: " + CountryUtil.getNameByCode(countryCode);
    }

    public String getHolderIdLabel() {
        return BankUtil.getHolderIdLabel(countryCode);
    }

    @Nullable
    public String getBankId() {
        return BankUtil.isBankIdRequired(countryCode) ? bankId : bankName;
    }
}
