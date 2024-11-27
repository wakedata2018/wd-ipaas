/**
 * 当前上下文
 */
import { Graph } from '@antv/g6';
import Command from './command';
import Editor from './editor';

export interface G6EditorInstance {
  graph: Graph;
  editor: Editor;
  command: Command;
}

/**
 * G6 实例
 */
let instance: G6EditorInstance | undefined;

export const getCurrentEditorContext = () => {
  return instance;
};

export const __setCurrentEditorContext = (g: G6EditorInstance) => {
  instance = g;
};
