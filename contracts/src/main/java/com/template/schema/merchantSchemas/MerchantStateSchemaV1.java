//package com.template.schema.merchantSchemas;
//
//import com.template.model.merchant.MerchantData;
//import com.template.schema.CBDCFamilySchema;
//import com.template.schema.UsersAccountPersistant;
//import lombok.Getter;
//import net.corda.core.schemas.MappedSchema;
//import net.corda.core.schemas.PersistentState;
//import org.hibernate.annotations.Type;
//
//import javax.annotation.Nullable;
//import javax.persistence.*;
//import java.util.Arrays;
//import java.util.List;
//import java.util.UUID;
//
//public class MerchantStateSchemaV1 extends MappedSchema {
//
//    public MerchantStateSchemaV1() {
//        super(CBDCFamilySchema.class, 1, Arrays.asList(PersistentMerchantStateSchema.class,
//                UsersAccountPersistant.class));
//    }
//
//    @Nullable
//    @Override
//    public String getMigrationResource() {
//        return "iou.changelog-master";
//    }
//
//    @Entity
//    @Table(name = "merchant")
//    @Getter
//    public static class PersistentMerchantStateSchema extends PersistentState {
//
//        @Column(name = "agreement")
//        private final String agreement;
//        @Column(name = "business_name")
//        private final String businessName;
//        @Column(name = "business_type")
//        private final String businessType;
//        @Column(name = "email")
//        private final String email;
//        @Column(name = "address")
//        private final String address;
//
//        @Column(name = "added_by")
//        private final String addedBy;
//        @Column(name = "owner")
//        private final String owner;
//        @Column(name = "linearid")
//        @Type(type = "uuid-char")
//        private final UUID linearId;
//
//
//        @OneToMany(targetEntity = UsersAccountPersistant.class, mappedBy = "persistentMerchantStateSchema",cascade = CascadeType.PERSIST)
//        private final List<UsersAccountPersistant> merchantAccounts;
//
//        public PersistentMerchantStateSchema(MerchantData merchantData, List<UsersAccountPersistant> usersAccountPersistants,
//                                             String addedBy, String owner, UUID linearId) {
//            this.addedBy = addedBy;
//            this.owner = owner;
//            this.linearId = linearId;
//            agreement = merchantData.getAgreement();
//            businessName =merchantData.getBusinessName();
//            businessType = merchantData.getBusinessType();
//            address = merchantData.getAddress();
//            email = merchantData.getEmail();
//            merchantAccounts = usersAccountPersistants;
//        }
//
//        // Default constructor required by hibernate.
//        public PersistentMerchantStateSchema() {
//            this.addedBy = null;
//            this.linearId = null;
//            this.owner = null;
//            agreement = null;
//            businessName = null;
//            businessType = null;
//            address = null;
//            email = null;
//            merchantAccounts = null;
//        }
//    }
//}
//
