package com.template.states.centralBanqueStates;

import com.template.contracts.centralBankContracts.CentralBankContract;
import com.template.model.centralBank.CentralBank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.corda.core.contracts.BelongsToContract;
import net.corda.core.contracts.LinearState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import net.corda.core.schemas.MappedSchema;
import net.corda.core.schemas.PersistentState;
import net.corda.core.schemas.QueryableState;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@AllArgsConstructor
@BelongsToContract(CentralBankContract.class)
public class CentralBankState implements LinearState  {

    private final CentralBank centralBank; // central bank dadta + Account
    private final Party centralBankNode; //the node where we store central bank info
    private final UniqueIdentifier linearId;

    @NotNull
    @Override
    public List<AbstractParty> getParticipants() {
        return Arrays.asList(centralBankNode);
    }


//    @NotNull
//    @Override
//    public PersistentState generateMappedObject(@NotNull MappedSchema schema) {
//        if (schema instanceof CentralBankStateSchemaV1) {
//            List<BankAccountPersistant> bankAccountPersistants = new ArrayList<>();
//            centralBank.getCentralBankAccount().forEach(centralBankAccount -> {
//                BankAccountPersistant bankAccountPersistant = new BankAccountPersistant();
//                bankAccountPersistant.setBankAccountPersistantId(null);
//                bankAccountPersistant.setAccountId(centralBankAccount.getAccountId());
//                bankAccountPersistant.setPassword(centralBankAccount.getPassword());
//                bankAccountPersistant.setAccountType(centralBankAccount.getAccountType());
//                bankAccountPersistant.setSuspend(centralBankAccount.isSuspend());
//                bankAccountPersistant.setCRUDDate(centralBankAccount.getCRUDDate());
//                bankAccountPersistants.add(bankAccountPersistant);
//            });
//            return new CentralBankStateSchemaV1.PersistentCentralBankStateSchema(
//                    this.centralBank.getCentralBankData(),
//                    bankAccountPersistants,
//                    centralBankNode.getName().toString(),
//                    this.linearId.getId());
//        } else {
//            throw new IllegalArgumentException("Le schema $schema est non reconnue");
//        }
//    }

//    @NotNull
//    @Override
//    public Iterable<MappedSchema> supportedSchemas() {
//        return Arrays.asList(new CentralBankStateSchemaV1());
//    }


}
