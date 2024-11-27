import { IEdge, INode, ITEM_TYPE, LayoutConfig } from '@antv/g6';
import { uniqueId } from './utils';
import Editor from './editor';

class Command {
  editor: Editor;
  undoList: { key: string; datas: any[] }[] = [];
  redoList: { key: string; datas: any[] }[] = [];

  constructor(editor: Editor) {
    this.editor = editor;
  }

  executeCommand(key: string, datas: any[]) {
    const list: any[] = [];
    datas.forEach(data => {
      let model = data;
      if (key === 'add') {
        if (model.id !== 'event_receive') {
          if (data.itemType === 'node') {
            model.id = uniqueId();
          } else {
            model.id = data.itemType + uniqueId();
          }
        }
      }
      if (key === 'delete') {
        if (data.getType() === 'node' || data.getType() === 'combo') {
          model = data.getModel();
          const edges = data.getEdges() as IEdge[];
          const id = data.getID();
          edges.forEach(edge => {
            const edgeModel = edge.getModel();
            const source = edge.getSource() as any;
            // 删除的节点不是线的源头节点时需要删除配置
            if (source !== data) {
              source?.getModel().form.dataAssetApi?.apiAttrs?.forEach((item: any) => {
                if (item.toOperatorId === id) {
                  item.oldToOperatorId = id;
                  item.toOperatorId = '';
                }
              });
            }
            list.push(edgeModel);
          });
          if (data.getType() === 'combo') {
            const nodes = data.getNodes() as INode[];
            nodes.forEach(node => {
              list.push(node.getModel());
            });
          }
        } else if (data.getType() === 'edge') {
          model = data.getModel();
          if (model.type === 'customEdge') {
            data
              .getSource()
              ?.getModel()
              .form.dataAssetApi?.apiAttrs?.forEach((item: any) => {
                if (item.toOperatorId === model.target) {
                  item.oldToOperatorId = model.target;
                  item.toOperatorId = '';
                }
              });
          }
        }
      }
      list.push(model);
      this.doCommand(key, model);
    });
    this.redoList = [];
    this.undoList.push({ key, datas: list });
    this.editor.emit(key, { undoList: this.undoList, redoList: this.redoList });
  }

  doCommand(key: string, data: any) {
    switch (key) {
      case 'add':
        this.add(data.itemType, data);
        break;
      case 'update':
        this.update(data.item, data.newModel);
        break;
      case 'delete':
        this.remove(data);
        break;
      case 'layout':
        this.updateLayout(data.after);
        break;
      default:
        break;
    }
  }

  add(type: ITEM_TYPE, item: any, isRedo?: boolean) {
    if (isRedo && item.type === 'customEdge') {
      const model: any = this.editor.getItemById(item.source)?.getModel();
      model?.form.dataAssetApi?.apiAttrs?.forEach((i: any) => {
        if (i.oldToOperatorId === item.target) {
          i.toOperatorId = item.target;
          delete i.oldToOperatorId;
        }
      });
    }
    this.editor.add(type, item, isRedo);
    if (isRedo && item.comboId) {
      this.editor.updateCombo(item.id, item.comboId, item.comboId);
    }
  }

  update(item: any, model: any, comboId?: string) {
    this.editor.update(item, model);
    if (comboId) {
      this.editor.updateCombo(model.id, model.comboId, comboId);
    }
  }

  remove(item: any, isRedo?: boolean) {
    if (isRedo && item.type === 'customEdge') {
      const model: any = this.editor.getItemById(item.source)?.getModel();
      model?.form.dataAssetApi?.apiAttrs?.forEach((i: any) => {
        if (i.toOperatorId === item.target) {
          i.oldToOperatorId = item.target;
          i.toOperatorId = '';
        }
      });
    }
    this.editor.remove(item);
  }

  updateLayout(layoutCfg: LayoutConfig) {
    this.editor.updateLayout(layoutCfg);
  }

  undo() {
    const undoData = this.undoList.pop();
    if (!undoData) {
      return;
    }

    const edgeList = [];
    const list = [];
    for (let i = 0; i < undoData.datas.length; i++) {
      const data = undoData.datas[i];
      if (data.itemType === 'edge') {
        edgeList.push(data);
        continue;
      }
      list.push(data);
      this.doundo(undoData.key, data);
    }
    for (let i = 0; i < edgeList.length; i++) {
      const edge = edgeList[i];
      list.push(edge);
      this.doundo(undoData.key, edge);
    }
    this.redoList.push({ key: undoData.key, datas: list });
    this.editor.emit(undoData.key, { undoList: this.undoList, redoList: this.redoList });
  }

  doundo(key: string, data: any) {
    switch (key) {
      case 'add':
        this.remove(data, true);
        break;
      case 'update':
        this.update(data.item, data.oldModel, data.newModel.comboId || data.oldModel.comboId);
        break;
      case 'delete':
        this.add(data.itemType, data, true);
        break;
      case 'layout':
        this.updateLayout(data.before);
        break;
      default:
    }
  }

  redo() {
    const redoData = this.redoList.pop();
    if (!redoData) {
      return;
    }

    const list = [];
    const edgeList = [];
    for (let i = 0; i < redoData.datas.length; i++) {
      const data = redoData.datas[i];
      if (data.itemType === 'edge') {
        edgeList.push(data);
        continue;
      }
      list.push(data);
      this.doredo(redoData.key, data);
    }
    for (let i = 0; i < edgeList.length; i++) {
      const edge = edgeList[i];
      list.push(edge);
      this.doredo(redoData.key, edge);
    }
    this.undoList.push({ key: redoData.key, datas: list });

    this.editor.emit(redoData.key, { undoList: this.undoList, redoList: this.redoList });
  }

  doredo(key: string, data: any) {
    switch (key) {
      case 'add':
        this.add(data.itemType, data, true);
        break;
      case 'update': {
        this.update(data.item, data.newModel, data.newModel.comboId || data.oldModel.comboId);
        break;
      }
      case 'delete': {
        this.remove(data, true);
        break;
      }
      case 'layout':
        this.updateLayout(data.after);
        break;
      default:
    }
  }

  delete(item: any) {
    this.executeCommand('delete', [item]);
  }

  updateItem(item: any, cfg: any) {
    this.editor.updateItem(item, cfg);
  }

  removeLastStack() {
    this.undoList.pop();
  }

  clearStack() {
    this.redoList = [];
    this.undoList = [];
  }
}

export default Command;
