import { mapState } from 'vuex';

export default {
  computed: {
    ...mapState({
      theme: state => state.theme
    })
  }
};
