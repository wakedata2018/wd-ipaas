const ViewConfigMutationsTypes = [
  'EDIT_INIT_STATE_CHANGE',
  'TYPE_CHANGE',
  'VERTICAL_CHANGE',
  'TOPNUM_CHANGE',
  'CUSTOM_CHANGE',
  'XAXIS_CHANGE',
  'YAXIS_CHANGE',
  'YAXIS_SERIES_CHANGE',
  'SERIES_CHANGE',
  'G_SERIES_CHANGE',
  'CATEGORY_CHANGE',
  'BUBBLE_SIZE_CHANGE',
  'SERIES_KEY_CHANGE'
];

const types = {};

ViewConfigMutationsTypes.forEach(item => {
  types[item] = item;
});

export default types;
