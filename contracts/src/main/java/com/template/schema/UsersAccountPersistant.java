//package com.template.schema;
//
//import com.template.schema.endUserSchemas.EndUserStateSchemaV1;
//import com.template.schema.merchantSchemas.MerchantStateSchemaV1;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//import javax.persistence.*;
//import java.util.Date;
//
//@Data
//@AllArgsConstructor
//@NoArgsConstructor
////@CordaSerializable
//@Entity
//@Table(name = "users_account_persistant")
//public class UsersAccountPersistant {
//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    @Column(name = "user_account_persistant_id")
//    private Long userAccountPersistantId;
//    @Column(name = "account_id")
//    private String accountId;
//    @Column(name = "password")
//    private String password;
//    @Column(name = "suspend")
//    private boolean suspend = false; // suspend = true the account is suspend, else false. false is default value
//    @Column(name = "account_type")
//    private String accountType ="courant"; // accountType = epargne or courant. courant by default
//    @Column(name = "bank_indcation")
//    private String bankIndcation; // indication de la banque: numero compte banque
//    @Column(name = "CRUD_date")
//    private Date CRUDDate;
//
//    @Column(name = "end_user_output_index")
//    private int end_user_output_index;
//    @Column(name = "end_user_transaction_id")
//    private String end_user_transaction_id;
//    @Column(name = "merchant_output_index")
//    private int merchant_output_index;
//    @Column(name = "merchant_transaction_id")
//    private String merchant_transaction_id;
//
//    @ManyToOne
//    @JoinColumns({
//            @JoinColumn(name = "end_user_output_index", referencedColumnName = "output_index"),
//            @JoinColumn(name = "end_user_transaction_id", referencedColumnName = "transaction_id"),
//    })
//    EndUserStateSchemaV1.PersistentEndUserStateSchema persistentEndUserStateSchema;
//    @ManyToOne
//    @JoinColumns({
//            @JoinColumn(name = "merchant_output_index", referencedColumnName = "output_index"),
//            @JoinColumn(name = "merchant_transaction_id", referencedColumnName = "transaction_id"),
//    })
//    MerchantStateSchemaV1.PersistentMerchantStateSchema persistentMerchantStateSchema;
//}
