import enLocale from "element-ui/lib/locale/lang/en";
import utils from "dss-common";

const en = {
 platform: {
  offine: "Offline Development",
  realtime: "Real-time Development",
  open: "Data Open Service",
  machineLearning: "Machine Learning",
  dataAsset: "Data Asset",
  priv: "Data Safty",
  chart: "Visualization",
 },
 validator: {
  noPermission: "No Permission. Please contact the administrators.",
 },
 ...enLocale,
 ...utils.en,
};

export default en;
