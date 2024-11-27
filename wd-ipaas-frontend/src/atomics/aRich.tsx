import { defineAtomicComponent, DefineAtomicProps, globalRegistry } from '@wakeadmin/components';
import Editor from '@tinymce/tinymce-vue';
/**
 * 定义原件 props
 */

export type ARichProps = DefineAtomicProps<string, {}>;

// 🔴 注册到全局的 AtomicProps 中
// 这个让 FatTable、FatForm 在使用时可以从 valueType 推断出 valueProps 的类型
declare global {
  interface AtomicProps {
    rich: ARichProps;
  }
}

export const ARichComponent = defineAtomicComponent(
  (props: ARichProps) => {
    return () => {
      const { value, onChange, mode } = props;
      const initConfig = {
        height: 500,
        width: 600,
        menubar: false,
        toolbar:
          'undo redo | blocks | ' +
          'bold italic backcolor | alignleft aligncenter ' +
          'alignright alignjustify | bullist numlist outdent indent | ' +
          'removeformat | help',
        content_style: 'body { font-family:Helvetica,Arial,sans-serif; font-size:16px }',
      };

      if (mode === 'preview') {
        return <div innerHTML={value}></div>;
      }

      return <Editor init={initConfig} value={value} onInput={onChange} />;
    };
  },
  {
    name: 'ARich',
  }
);

globalRegistry.register('rich', {
  name: 'rich',
  component: ARichComponent,
  description: '富文本编辑器',
});
