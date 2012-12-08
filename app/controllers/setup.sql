create table dpss_pricing_type (
  promotion_run_id                number not null,
  pricing_type                    number not null,
  time_frame                      number not null,
  description                     varchar2(50) not null,
  comments                        varchar2(1000),
  created_by                      varchar2(100),
  created_date                    Date,
  hide_in_selection_yn            varchar2(1 byte) default 'N' not null,
  product_family_enabled_yn       varchar2(1 byte) default 'N' not null,
  auto_schedule_first_sale_yn     varchar2(1 byte) default 'N' not null,
  calculation_order               number,
  default_calc_method_metric_id   number,
  scheduled_due_date_code_id      number,
  constraint pk_dpss_pt_1 primary key (promotion_run_id, dps_pricing_type),
  constraint fk_dpss_pt_1 foreign key (promotion_run_id) references gp_data_promo_run (promotion_run_id),
  constraint fk_dpss_pt_2 foreign key (default_calc_method_metric_id) references gp_metric (metric_id),
  constraint ct_dpss_pt_1 check (time_frame in ( 1, 2, 3, 4 )),
  constraint ct_dpss_pt_2 check (scheduled_due_date_code_id in ( 260, 261, 262 ))
);

create table dpss_pt_priceapproval (
  promotion_run_id        number not null,
  pricing_type_id         number not null,
  pd_allow_or             varchar2(1 byte) default 'N',
  pd_allow_bd             varchar2(1 byte) default 'N',
  od_allow_or             varchar2(1 byte) default 'N',
  od_allow_bd             varchar2(1 byte) default 'N',
  constraint pk_dpss_pt_cfg_1 primary key (promotion_run_id, pricing_type_id),
  constraint fk_dpss_pt_cfg_2 foreign key (promotion_run_id, pricing_type_id)
    references dpss_pricing_type (promotion_run_id, dpss_pricing_type)
);

DECLARE
  h1 NUMBER;              
  v_date varchar2(20);
  v_date_started date;
BEGIN
    v_date_started := sysdate;
    v_date := to_char(v_date_started,'YYYYMMDD-HH24MISS');
    h1 := DBMS_DATAPUMP.OPEN('EXPORT', 'TABLE', null, 'job2'||v_date, 'COMPATIBLE'); 
	DBMS_DATAPUMP.add_file(h1,'exp_dp.dmp', 'DATA_PUMP_DIR'); 
    DBMS_DATAPUMP.METADATA_FILTER(h1,'SCHEMA_EXPR','IN (''ECMUSER_epbyminw2300_gp'')');
	DBMS_DATAPUMP.METADATA_FILTER(h1,'NAME_EXPR','=''GP_PRICING_TYPE''');
    DBMS_DATAPUMP.start_job(h1); 
    DBMS_DATAPUMP.detach(h1); 
END;
/